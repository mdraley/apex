import fs from 'fs';
import path from 'path';

const projectRoot = process.cwd();

const lightRawPath = path.resolve(projectRoot, 'src/theme/figma/light.raw.json');
const darkRawPath = path.resolve(projectRoot, 'src/theme/figma/dark.raw.json');
const mappingConfigPath = path.resolve(projectRoot, 'src/theme/figma/mapping.config.json');
const outputLightPath = path.resolve(projectRoot, 'src/theme/apex-light.json');
const outputDarkPath = path.resolve(projectRoot, 'src/theme/apex-dark.json');

function mapTokens(rawTokens, mapping) {
  const token = {};
  const components = {};
  if (mapping.token) {
    for (const [key, paths] of Object.entries(mapping.token)) {
      for (const p of paths) {
        const value = p.split('.').reduce((obj, k) => (obj ? obj[k] : undefined), rawTokens);
        if (value !== undefined) {
          token[key] = value;
          break;
        }
      }
    }
  }
  if (mapping.components) {
    for (const [compKey, paths] of Object.entries(mapping.components)) {
      const [compName, prop] = compKey.split('.');
      components[compName] = components[compName] || {};
      for (const p of paths) {
        const value = p.split('.').reduce((obj, k) => (obj ? obj[k] : undefined), rawTokens);
        if (value !== undefined) {
          components[compName][prop] = value;
          break;
        }
      }
    }
  }
  return { theme: { token, components } };
}

function build() {
  const mapping = JSON.parse(fs.readFileSync(mappingConfigPath, 'utf8'));
  const lightRaw = JSON.parse(fs.readFileSync(lightRawPath, 'utf8'));
  const darkRaw = JSON.parse(fs.readFileSync(darkRawPath, 'utf8'));

  const lightTheme = mapTokens(lightRaw, mapping);
  const darkTheme = mapTokens(darkRaw, mapping);

  fs.writeFileSync(outputLightPath, JSON.stringify(lightTheme, null, 2));
  fs.writeFileSync(outputDarkPath, JSON.stringify(darkTheme, null, 2));
  console.log('Theme JSONs generated!');
}

build();
