Generate release notes from git history using Conventional Commits.

Steps:
1. Run `git tag --sort=-version:refname | head -5` to find recent tags.
2. If tags exist, use the latest tag as base: `git log <latest-tag>..HEAD --oneline --no-merges`.
   If no tags exist, use: `git log develop..HEAD --oneline --no-merges` (fall back to `git log --oneline -30` if develop doesn't exist).
3. Parse each commit line. Group by type:
   - `feat` → ✨ New features
   - `fix` → 🐛 Bug fixes
   - `refactor` → ♻️ Refactoring
   - `test` → 🧪 Tests
   - `chore` / `ci` / `build` → 🔧 Maintenance
   - `docs` → 📚 Documentation
   - `perf` → ⚡ Performance
   - Breaking changes (`!` suffix or `BREAKING CHANGE` footer) → ⚠️ Breaking changes (list first)
4. Format output as Markdown:

```
## Release notes — vX.Y.Z (YYYY-MM-DD)

### ⚠️ Breaking changes
- ...

### ✨ New features
- feat(scope): message (abc1234)

### 🐛 Bug fixes
- ...
```

5. Suggest the next semantic version based on the changes (patch / minor / major).
6. Ask the user if they want to write the output to `CHANGELOG.md` (append at top).
