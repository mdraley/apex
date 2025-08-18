# TDD Green Phase - Make Tests Pass Quickly

Write the minimal code necessary to satisfy GitHub issue requirements and make failing tests pass. Resist the urge to write more than required.

## GitHub Issue Integration
• Reference issue context - Keep GitHub issue requirements in focus during implementation
• Validate against acceptance criteria - Ensure implementation meets issue definition of done
• Track progress - Update issue with implementation progress and blockers
• Stay in scope - Implement only what's required by current issue, avoid scope creep

## Core Principles
### Minimal Implementation
• Just enough code - Implement only what's needed to satisfy issue requirements and make tests pass
• Fake it till you make it - Start with hard-coded returns based on issue examples, then generalise
• Obvious implementation - When the solution is clear from issue, implement it directly
• Triangulation - Add more tests based on issue scenarios to force generalisation

### Speed Over Perfection
• Green bar quickly - Prioritise making tests pass over code quality
• Ignore code smells temporarily - Duplication and poor design will be addressed in refactor phase
• Simple solutions first - Choose the most straightforward implementation path from issue context
• Defer complexity - Don't anticipate requirements beyond current issue scope

## Execution Guidelines
1. Review issue requirements - Confirm implementation aligns with GitHub issue acceptance criteria
2. Run the failing test - Confirm exactly what needs to be implemented
3. Confirm your plan with the user - Ensure understanding of requirements and edge cases. NEVER start making changes without user confirmation
4. Write minimal code - Add just enough to satisfy issue requirements and make test pass
5. Run all tests - Ensure new code doesn't break existing functionality
6. Do not modify the test - Ideally the test should not need to change in the Green phase.
7. Update issue progress - Comment on implementation status if needed

## Green Phase Checklist
- Implementation aligns with GitHub issue requirements
- All tests are passing (green bar)
- No more code written than necessary for issue scope
- Existing tests remain unbroken
- Implementation is simple and direct
- Issue acceptance criteria satisfied
- Ready for refactoring phase
