---
mode: "agent"
description: "Activate Beast Mode for this workspace, load .github/instructions/copilot-instructions.md, and then suggest relevant prompts from the awesome-copilot repo without duplicates. Context: Java 17, Spring Boot 3.x, Maven; layered packages (controller, service, dto, config, client, util); SOAP/CXF + Vault. All actions must follow Beast Mode rules."
tools: ["changes","codebase","editFiles","fetch","findTestFiles","githubRepo","new","openSimpleBrowser","problems","runCommands","runTasks","runTests","search","searchResults","terminalLastCommand","terminalSelection","testFailure","usages","vscodeAPI"]
---


# Start Beast Mode for This Repository

1) Attach Beast-Mode instructions  
   `#.github/instructions/copilot-instructions.md`

2) Attach the prompt suggester  
   `#.github/prompts/suggest-awesome-github-copilot-prompts.prompt.md`

3) Ask:  
   “Analyze this repo with Beast Mode active and suggest relevant awesome-copilot prompts (no duplicates). 
   Prefer prompts for Java 17, Spring Boot, Maven, SOAP/CXF, Vault, testing, and docs.”

4) When I confirm, add the selected prompt files into `.github/prompts/` for persistent use.
