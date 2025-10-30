# Contributing to ContractParser

Thank you for your interest in contributing to ContractParser! This document provides guidelines and instructions for contributing to the project.

## 🤝 How to Contribute

### Reporting Bugs

If you find a bug, please create an issue on GitHub with the following information:

1. **Description**: Clear description of the bug
2. **Steps to Reproduce**: Detailed steps to reproduce the issue
3. **Expected Behavior**: What you expected to happen
4. **Actual Behavior**: What actually happened
5. **Environment**: 
   - OS (macOS, Windows, Linux)
   - Java version
   - Maven version
6. **Screenshots**: If applicable
7. **Error Logs**: Any relevant error messages or stack traces

### Suggesting Features

We welcome feature suggestions! Please create an issue with:

1. **Clear Title**: Brief description of the feature
2. **Problem Statement**: What problem does this solve?
3. **Proposed Solution**: How would you like to see this implemented?
4. **Alternatives**: Any alternative solutions you've considered
5. **Additional Context**: Screenshots, mockups, or examples

### Pull Requests

#### Before You Start

1. **Check Existing Issues**: Make sure someone isn't already working on it
2. **Create an Issue**: Discuss your proposed changes first
3. **Fork the Repository**: Create your own fork to work in

#### Development Setup

```bash
# Fork the repo and clone your fork
git clone https://github.com/YOUR_USERNAME/ContractParser.git
cd ContractParser

# Add upstream remote
git remote add upstream https://github.com/ORIGINAL_OWNER/ContractParser.git

# Install dependencies
mvn clean install

# Create .env file with AWS credentials
cp .env.example .env
# Edit .env with your credentials
```

#### Making Changes

1. **Create a Branch**: Use a descriptive name
   ```bash
   git checkout -b feature/your-feature-name
   # or
   git checkout -b fix/bug-description
   ```

2. **Follow Code Style**:
   - Use meaningful variable and method names
   - Add comments for complex logic
   - Follow Java naming conventions
   - Keep methods focused and concise

3. **Write Clean Code**:
   - Maintain existing code structure
   - Don't introduce unnecessary dependencies
   - Handle exceptions appropriately
   - Validate user inputs

4. **Test Your Changes**:
   ```bash
   # Build the project
   mvn clean package
   
   # Run the application
   java -jar target/ContractParser-1.0-SNAPSHOT.jar
   
   # Test all features:
   # - Image upload
   # - Text extraction
   # - Data review/editing
   # - Contract generation
   ```

5. **Commit Your Changes**:
   ```bash
   git add .
   git commit -m "Add: Brief description of your change"
   ```
   
   Use conventional commit messages:
   - `Add: New feature`
   - `Fix: Bug fix`
   - `Update: Changes to existing feature`
   - `Refactor: Code refactoring`
   - `Docs: Documentation changes`
   - `Style: Code style changes`

6. **Keep Your Fork Updated**:
   ```bash
   git fetch upstream
   git rebase upstream/main
   ```

7. **Push Your Changes**:
   ```bash
   git push origin feature/your-feature-name
   ```

8. **Create Pull Request**:
   - Go to your fork on GitHub
   - Click "New Pull Request"
   - Provide detailed description of changes
   - Reference related issues

#### Pull Request Guidelines

Your PR should:

- ✅ Have a clear, descriptive title
- ✅ Reference related issue(s): "Fixes #123" or "Closes #456"
- ✅ Include description of changes and why they're needed
- ✅ Be tested and working
- ✅ Not break existing functionality
- ✅ Follow the existing code style
- ✅ Include comments for complex logic
- ✅ Update documentation if needed

## 🎯 Areas for Contribution

### High Priority
- [ ] Add support for other ID card formats (EU, US, etc.)
- [ ] Implement database storage for generated contracts
- [ ] Add export to PDF functionality
- [ ] Improve error handling and user feedback
- [ ] Add unit and integration tests

### Medium Priority
- [ ] Support for multiple languages
- [ ] Template editor within the application
- [ ] Batch processing multiple ID cards
- [ ] Contract versioning system
- [ ] Search and filter functionality for archived contracts

### Nice to Have
- [ ] Dark mode UI theme
- [ ] Custom placeholder configuration
- [ ] Email integration for sending contracts
- [ ] Digital signature support
- [ ] Cloud storage integration (Google Drive, Dropbox)

## 🧪 Testing Guidelines

Currently, the project lacks automated tests. If you'd like to contribute tests:

1. **Unit Tests**: Test individual methods and classes
   - Use JUnit 5 (already included)
   - Place tests in `src/test/java/`
   - Follow naming: `ClassNameTest.java`

2. **Integration Tests**: Test component interactions
   - Test UI components separately
   - Mock AWS Textract calls when possible
   - Test document generation with sample data

3. **Test Coverage**: Aim for reasonable coverage
   - Focus on business logic first
   - Test error handling paths
   - Test edge cases

## 📝 Code Style

### Java Conventions
- Use 4 spaces for indentation (no tabs)
- Opening braces on same line
- Use descriptive variable names
- Add JavaDoc for public methods

Example:
```java
/**
 * Generates an employment contract from template and provided data.
 *
 * @param templatePath Path to the contract template file
 * @param outputPath Path where the generated contract will be saved
 * @param data Map of placeholder-value pairs to replace in template
 * @throws IOException if file operations fail
 */
public static void generateContract(String templatePath, String outputPath, 
                                    Map<String, String> data) throws IOException {
    // Implementation
}
```

### JavaFX UI
- Keep UI logic separate from business logic
- Use meaningful IDs for UI components
- Follow JavaFX best practices
- Use CSS where appropriate for styling

## 🔒 Security

### Important Rules
- **NEVER** commit AWS credentials
- **NEVER** commit `.env` files
- **ALWAYS** check commits before pushing
- Report security vulnerabilities privately

### Handling Sensitive Data
- Use `.env` for all credentials
- Add sensitive files to `.gitignore`
- Don't log sensitive information
- Sanitize user inputs

## 📚 Resources

### Project-Specific
- [JavaFX Documentation](https://openjfx.io/)
- [Apache POI Documentation](https://poi.apache.org/)
- [AWS Textract Documentation](https://docs.aws.amazon.com/textract/)

### General
- [Git Best Practices](https://git-scm.com/doc)
- [Java Code Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-contents.html)
- [Maven Documentation](https://maven.apache.org/guides/)

## ❓ Questions?

If you have questions about contributing:
1. Check existing issues and discussions
2. Create a new issue with the "question" label
3. Be patient and respectful

## 📜 Code of Conduct

### Our Standards
- Be respectful and inclusive
- Accept constructive criticism gracefully
- Focus on what's best for the project
- Show empathy towards others

### Unacceptable Behavior
- Harassment or discriminatory comments
- Trolling or insulting comments
- Personal or political attacks
- Publishing others' private information

## 🙏 Thank You!

Your contributions make this project better. Whether it's a bug report, feature suggestion, or pull request, we appreciate your time and effort!

---

Happy coding! 🚀

