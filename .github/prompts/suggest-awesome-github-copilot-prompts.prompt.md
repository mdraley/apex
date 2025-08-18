---
mode: "agent"
description: "Suggest relevant GitHub Copilot prompt files from the awesome-copilot repo based on current repo context and chat history, avoiding duplicates. Context: Java 17, Spring Boot 3.x, Maven; layered packages (controller, service, dto, config, client, util); integrates with SOAP/CXF and Vault. Follow Beast Mode rules in .github/instructions/copilot-instructions.md."
tools: ["changes","codebase","editFiles","fetch","findTestFiles","githubRepo","new","openSimpleBrowser","problems","runCommands","runTasks","runTests","search","searchResults","terminalLastCommand","terminalSelection","testFailure","usages","vscodeAPI"]
---

# Suggest Awesome GitHub Copilot Prompts

Analyze current repository context and suggest relevant prompt files from the
[awesome-copilot repository](https://github.com/github/awesome-copilot/tree/main/prompts)
that are not already available in this repository.

## Scope
Scan only `.github/prompts/` in this workspace (e.g., `backend/.github/prompts/` if present).

## Process
1. Fetch list/metadata of awesome-copilot prompts (from the official README).
2. Enumerate local `*.prompt.md` files in `.github/prompts/` and read their front-matter descriptions.
3. Compare and filter to avoid duplicates.
4. Prioritize prompts useful for: Java 17, Spring Boot, Maven, CXF/SOAP, Vault, testing, docs, reviews.
5. Output a table: Prompt | Description | Already Installed | Similar Local | Rationale.
6. On approval, download/insert selected prompts into `.github/prompts/` (and link them in the output).
