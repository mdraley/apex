// Simple generator: reads pom.xml and frontend package.json, emits MD/JSON/YAML
import { promises as fs } from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { parseStringPromise } from 'xml2js';
import yaml from 'js-yaml';

// Resolve paths relative to this script to avoid hardcoded absolute paths
const __filename = fileURLToPath(import.meta.url);
const GEN_ROOT = path.dirname(__filename); // .../apex-idp/docs/blueprint-generator
const DOCS_ROOT = path.resolve(GEN_ROOT, '..'); // .../apex-idp/docs
const REPO_ROOT = path.resolve(GEN_ROOT, '../../'); // .../apex-idp
const API_ROOT = path.resolve(REPO_ROOT, 'apex-idp-api');
const APP_ROOT = path.resolve(REPO_ROOT, 'apex-idp-app');

async function readPom(pomPath) {
  const xml = await fs.readFile(pomPath, 'utf8');
  const obj = await parseStringPromise(xml);
  return obj;
}

async function readJson(file) {
  return JSON.parse(await fs.readFile(file, 'utf8'));
}

function coalesce(x, def) { return x ?? def; }

function extractSpring(obj) {
  const parentVersion = obj.project.parent?.[0]?.version?.[0];
  const props = obj.project.properties?.[0] || {};
  const springBoot = parentVersion || 'unknown';
  const springCloud = props['spring-cloud.version']?.[0] || 'unknown';
  const springdoc = props['springdoc.version']?.[0] || 'unknown';
  const mapstruct = props['mapstruct.version']?.[0] || 'unknown';
  const lombok = props['lombok.version']?.[0] || 'unknown';
  return { springBoot, springCloud, springdoc, mapstruct, lombok };
}

function md(tech, modules) {
  const modulesList = modules.map(m => `- ${m.name || m.artifactId} (${m.packaging || 'jar'})`).join('\n');
  const depsByModule = modules.map(m => {
    const title = `### ${m.name || m.artifactId}`;
    const deps = (m.dependencies && m.dependencies.length)
      ? m.dependencies.slice(0,10).map(d => `- ${d.groupId}:${d.artifactId}${d.version ? ':'+d.version : ''}${d.scope ? ' ('+d.scope+')' : ''}`).join('\n')
      : '_No dependencies listed_';
    return `${title}\n${deps}`;
  }).join('\n\n');

  return `# Technology Stack Blueprint\n\nConfiguration\n- Project Type: Auto-detect (full-stack)\n- Depth Level: Comprehensive\n- Include Versions: true\n- Include Licenses: true\n- Include Diagrams: true\n- Include Usage Patterns: true\n- Include Conventions: true\n- Output Format: Markdown\n- Categorization: Layer\n\n## Versions\n- Spring Boot ${tech.springBoot}\n- Spring Cloud ${tech.springCloud}\n- springdoc-openapi ${tech.springdoc}\n- MapStruct ${tech.mapstruct}\n- Lombok ${tech.lombok}\n- React ${tech.react}\n- Vite ${tech.vite}\n- TypeScript ${tech.typescript}\n\n## Endpoints\n- Health: /api/actuator/health\n- Swagger UI: /api/swagger-ui/index.html\n- OpenAPI: /api/v3/api-docs\n\n## Modules\n${modulesList}\n\n## Dependencies by Module (top 10)\n${depsByModule}\n`;
}

async function main() {
  const parentPom = await readPom(path.join(API_ROOT, 'pom.xml'));
  const appPkg = await readJson(path.join(APP_ROOT, 'package.json'));
  const tech = extractSpring(parentPom);
  tech.react = appPkg.dependencies?.react ?? 'unknown';
  tech.vite = appPkg.devDependencies?.vite ?? 'unknown';
  tech.typescript = appPkg.devDependencies?.typescript ?? 'unknown';

  // Collect modules from parent pom
  const moduleNames = parentPom.project.modules?.[0]?.module || [];
  const modules = [];
  for (const mod of moduleNames) {
    try {
      const modPom = await readPom(path.join(API_ROOT, mod, 'pom.xml'));
      const proj = modPom.project || {};
      const name = proj.name?.[0];
      const artifactId = proj.artifactId?.[0] || mod;
      const packaging = proj.packaging?.[0] || 'jar';
      const deps = (proj.dependencies?.[0]?.dependency || []).map(d => ({
        groupId: d.groupId?.[0] || '',
        artifactId: d.artifactId?.[0] || '',
        version: d.version?.[0] || '',
        scope: d.scope?.[0] || ''
      }));
      modules.push({ name, artifactId, packaging, dependencies: deps });
    } catch (e) {
      modules.push({ artifactId: mod, packaging: 'jar', dependencies: [] });
    }
  }

  // JSON
  const jsonOut = {
    configuration: {
      projectType: 'Auto-detect', depthLevel: 'Comprehensive',
      includeVersions: true, includeLicenses: true, includeDiagrams: true,
      includeUsagePatterns: true, includeConventions: true,
      outputFormat: 'Markdown', categorization: 'Layer'
    },
    versions: tech,
    modules,
    endpoints: {
      health: '/api/actuator/health', swaggerUI: '/api/swagger-ui/index.html', openAPISpec: '/api/v3/api-docs'
    }
  };

  const mdOut = md(tech, modules);
  const yamlOut = yaml.dump(jsonOut, { noRefs: true });

  await fs.writeFile(path.join(DOCS_ROOT, 'Technology_Stack_Blueprint.json'), JSON.stringify(jsonOut, null, 2));
  await fs.writeFile(path.join(DOCS_ROOT, 'Technology_Stack_Blueprint.yaml'), yamlOut);
  await fs.writeFile(path.join(DOCS_ROOT, 'Technology_Stack_Blueprint.md'), mdOut);

  console.log('Blueprint regenerated at', new Date().toISOString());
}

main().catch(err => { console.error(err); process.exit(1); });
