Audit the GitHub Actions CI/CD configuration for this project.

Steps:
1. List all workflow files: `find .github/workflows -name "*.yml" -o -name "*.yaml" 2>/dev/null`.
2. Read each workflow file in full.
3. Also read `CLAUDE.md` for the declared CI expectations (branches, required steps).

Audit each workflow against these criteria:

**Security**
- [ ] Actions pinned to a full SHA commit hash (not a mutable tag like `v3` or `latest`)
- [ ] `GITHUB_TOKEN` permissions declared explicitly (`permissions:` block)
- [ ] No secrets printed in logs (`echo $SECRET`, etc.)
- [ ] `pull_request_target` used only when necessary (risk of privilege escalation)

**Correctness**
- [ ] `mvn test` runs before any deploy or publish step
- [ ] Java version matches the project (Java 21)
- [ ] Matrix or explicit OS specified
- [ ] Caching of Maven dependencies (`~/.m2`) configured

**Coverage**
- [ ] PRs to `develop` and `main` both trigger the pipeline
- [ ] CodeQL analysis present (declared in CLAUDE.md)
- [ ] No branch left unprotected by CI

**Quality**
- [ ] Workflow names are descriptive
- [ ] Jobs have explicit `timeout-minutes` to avoid runaway builds
- [ ] Fail-fast behavior appropriate for the job type

Output a prioritized report:
- 🔴 Critical (security / build correctness)
- 🟠 Important (coverage gaps)
- 🟡 Nice-to-have (quality / hygiene)

For each finding include: the file + line, what the issue is, and a concrete fix.
