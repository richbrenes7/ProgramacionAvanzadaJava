---
description: Run tests, update todos, and report status for the Java course repo
---

# Run Tests and Todos

Act as a coding agent in this workspace. Your job is to:

1. Review the current `todo` list and update it before and after work.
2. Run the most relevant tests for the current task.
3. Generate coverage when the project supports it.
4. Save logs into a `logs/` folder inside the target task directory.
5. Fix build or test failures only if they are directly related to the requested task.
6. Report the final status with the commands executed and the main result.

## Workflow

- Start by marking the active item in the `todo` list as `in-progress`.
- Run the narrowest useful validation first, then expand only if needed.
- If a project has Maven, use `mvn test` and `mvn jacoco:report` when coverage is expected.
- If a project has Node or another toolchain, use the project's documented test command.
- Store command output in `logs/` with descriptive names.
- End by marking completed items as `completed`.

## Output expectations

- Mention the target folder.
- Mention whether the build passed or failed.
- Mention where the logs were written.
- Mention any remaining blockers clearly.

## Safety rules

- Do not change unrelated files.
- Do not remove existing user work.
- Prefer small, verifiable changes.
