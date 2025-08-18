---
mode: "agent"
description: "Suggest relevant GitHub Copilot chatmode files from the awesome-copilot repository based on current repository context and chat history, avoiding duplicates with existing chatmodes in this repository."
---

# Suggest Awesome GitHub Copilot Chatmodes

## Process
1. Fetch Available Chatmodes: Extract chatmode list and descriptions from [awesome-copilot chatmodes folder](https://github.com/github/awesome-copilot/tree/main/chatmodes)
2. Scan Local Chatmodes: Discover existing chatmode files in `.github/chatmodes/` folder
3. Extract Descriptions: Read front matter from local chatmode files to get descriptions
4. Analyze Context: Review chat history, repository files, and current project needs
5. Compare Existing: Check against chatmodes already available in this repository
6. Match Relevance: Compare available chatmodes against identified patterns and requirements
7. Present Options: Display relevant chatmodes with descriptions, rationale, and availability status
8. Validate: Ensure suggested chatmodes would add value not already covered by existing chatmodes
9. Output: Provide structured table with suggestions, descriptions, and links to both awesome-copilot chatmodes and similar local chatmodes
10. Next Steps: If any suggestions are made, provide instructions that GitHub Copilot will be able to follow to add the suggested chatmodes to the repository by downloading the file into the chatmodes directory. Offer to do this automatically if the user confirms.

## Context Analysis Criteria
🔍 Repository Patterns:
• Programming languages used (.cs, .js, .py, etc.)
• Framework indicators (ASP.NET, React, Azure, etc.)
• Project types (web apps, APIs, libraries, tools)
• Documentation needs (README, specs, ADRs)

🗨️ Chat History Context:
• Recent discussions and pain points
• Feature requests or implementation needs
• Code review patterns
• Development workflow requirements

## Output Format
Display analysis results in structured table comparing awesome-copilot chatmodes with existing repository chatmodes:
| code-reviewer.chatmode.md | Specialized code review chatmode | ❌ No | None | Would enhance development workflow with dedicated code review assistance |
| architect.chatmode.md | Software architecture guidance | ✅ Yes | azure_principal_architect.chatmode.md | Already covered by existing architecture chatmodes |
| debugging-expert.chatmode.md | Debug assistance chatmode | ❌ No | None | Could improve troubleshooting efficiency for development team |

## Local Chatmodes Discovery Process
1. List all `*.chatmode.md` files in `.github/chatmodes/` directory
2. For each discovered file, read front matter to extract `description`
3. Build comprehensive inventory of existing chatmodes
4. Use this inventory to avoid suggesting duplicates

## Requirements
• Use `githubRepo` tool to get content from awesome-copilot repository chatmodes folder
• Scan local file system for existing chatmodes in `.github/chatmodes/` directory
• Read YAML front matter from local chatmode files to extract descriptions
• Compare against existing chatmodes in this repository to avoid duplicates
• Focus on gaps in current chatmode library coverage
• Validate that suggested chatmodes align with repository's purpose and standards
• Provide clear rationale for each suggestion
• Include links to both awesome-copilot chatmodes and similar local chatmodes
• Don't provide any additional information or context beyond the table and the analysis

## Icons Reference
• ✅ Already installed in repo
• ❌ Not installed in repo
