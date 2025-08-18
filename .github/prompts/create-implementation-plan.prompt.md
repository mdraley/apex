---
mode: "agent"
description: "Create a new implementation plan file for new features, refactoring existing code or upgrading packages, design, architecture or infrastructure."
---

# Create Implementation Plan

## Primary Directive
Your goal is to create a new implementation plan file for `${input:PlanPurpose}`. Your output must be machine-readable, deterministic, and structured for autonomous execution by other AI systems or humans.

## Execution Context
This prompt is designed for AI-to-AI communication and automated processing. All instructions must be interpreted literally and executed systematically without human interpretation or clarification.

## Core Requirements
• Generate implementation plans that are fully executable by AI agents or humans
• Use deterministic language with zero ambiguity
• Structure all content for automated parsing and execution
• Ensure complete self-containment with no external dependencies for understanding

## Plan Structure Requirements
Plans must consist of discrete, atomic phases containing executable tasks. Each phase must be independently processable by AI agents or humans without cross-phase dependencies unless explicitly declared.
• Each phase must have measurable completion criteria
• Tasks within phases must be executable in parallel unless dependencies are specified
• All task descriptions must include specific file paths, function names, and exact implementation details
• No task should require human interpretation or decision-making
• Use explicit, unambiguous language with zero interpretation required
• Structure all content as machine-parseable formats (tables, lists, structured data)
• Include specific file paths, line numbers, and exact code references where applicable
• Define all variables, constants, and configuration values explicitly
• Provide complete context within each task description
• Use standardized prefixes for all identifiers (REQ-, TASK-, etc.)
• Include validation criteria that can be automatically verified

## Output File Specifications
• Save implementation plan files in `/plan/` directory
• Use naming convention: `[purpose]-[component]-[version].md`
• Purpose prefixes: `upgrade|refactor|feature|data|infrastructure|process|architecture|design`
• Example: `upgrade-system-command-4.md`, `feature-auth-module-1.md`
• File must be valid Markdown with proper front matter structure

## Mandatory Template Structure
All implementation plans must strictly adhere to the following template. Each section is required and must be populated with specific, actionable content. AI agents must validate template compliance before execution.
• All front matter fields must be present and properly formatted
• All section headers must match exactly (case-sensitive)
• All identifier prefixes must follow the specified format
• Tables must include all required columns
• No placeholder text may remain in the final output

## Status
The status of the implementation plan must be clearly defined in the front matter and must reflect the current state of the plan. The status can be one of the following (status_color in brackets): `Completed` (bright green badge), `In progress` (yellow badge), `Planned` (blue badge), `Deprecated` (red badge), or `On Hold` (orange badge). It should also be displayed as a badge in the introduction section.

---
goal: [Concise Title Describing the Package Implementation Plan's Goal]
version: [Optional: e.g., 1.0, Date]
date_created: [YYYY-MM-DD]
last_updated: [Optional: YYYY-MM-DD]
owner: [Optional: Team/Individual responsible for this spec]
status: 'Completed'|'In progress'|'Planned'|'Deprecated'|'On Hold'
tags: [Optional: List of relevant tags or categories, e.g., `feature`, `upgrade`, `chore`, `architecture`, `migration`, `bug` etc]
---

# Introduction

## 1. Requirements & Constraints
[Explicitly list all requirements & constraints that affect the plan and constrain how it is implemented. Use bullet points or tables for clarity.]

## 2. Implementation Steps
### Implementation Phase 1
| Task | Description | Completed | Date |
|------|-------------|-----------|------|
| TASK-001 | Description of task 1 | | |
### Implementation Phase 2
| Task | Description | Completed | Date |
|------|-------------|-----------|------|
| TASK-002 | Description of task 2 | | |

## 3. Alternatives
[A bullet point list of any alternative approaches that were considered and why they were not chosen.]

## 4. Dependencies
[List any dependencies that need to be addressed, such as libraries, frameworks, or other components that the plan relies on.]

## 5. Files
[List the files that will be affected by the feature or refactoring task.]

## 6. Testing
[List the tests that need to be implemented to verify the feature or refactoring task.]

## 7. Risks & Assumptions
[List any risks or assumptions related to the implementation of the plan.]

## 8. Related Specifications / Further Reading
[Link to related spec 1]
[Link to relevant external documentation]
