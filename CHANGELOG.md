# Changelog

All notable changes to the ContractParser project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0-SNAPSHOT] - Current Development

### ✨ Features
- ID card OCR recognition using AWS Textract
- Automatic extraction of personal information from Romanian ID cards
- Interactive data review and editing interface
- Employment contract generation from Word templates
- Employee information sheet (fisa) generation
- Configurable salary settings via `config.yml`
- Archive system for generated documents
- Modern glass-morphism UI design
- Form validation with user-friendly error messages
- Support for JPG and PNG image formats

### 🏗 Technical
- JavaFX 21.0.1 desktop application
- Apache POI for Word document processing
- AWS Textract SDK integration
- Maven Shade plugin for uber JAR creation
- Java 17 compatibility
- Launcher pattern for proper JAR execution

### 📄 Templates
- Employment contract template with placeholder system
- Employee information sheet template
- Support for 14 different placeholder fields

### 🔧 Configuration
- External `config.yml` for salary configuration
- `.env` file for AWS credentials management
- Automatic configuration file creation on first run

### 🎨 UI Components
- Image upload with file chooser
- Multi-field input forms
- Two-page workflow (data entry → review)
- Back navigation support
- Success/error alert dialogs

### 📁 File Organization
- Automatic `arhiva` folder creation
- Organized document naming: `{Name}.docx` and `{Name}_fisa.docx`
- Date formatting (dd.MM.yyyy)
- Phone number formatting (0000 000 000)

### 🐛 Known Issues
- Template files must be embedded in resources before JAR build
- AWS credentials required for OCR functionality
- Limited to Romanian ID card format
- No database persistence
- No PDF export functionality

### 🔄 Future Improvements
- Add unit tests
- Implement database storage
- Add PDF export
- Support for multiple ID card formats
- Multi-language support
- Batch processing capability

---

## Version History Guidelines

### Types of Changes
- `Added` - New features
- `Changed` - Changes in existing functionality
- `Deprecated` - Soon-to-be removed features
- `Removed` - Removed features
- `Fixed` - Bug fixes
- `Security` - Vulnerability fixes

### Version Format
- Major version (X.0.0) - Incompatible API changes
- Minor version (0.X.0) - Backwards-compatible new features
- Patch version (0.0.X) - Backwards-compatible bug fixes

---

**Note**: This is an initial development version. Stable releases will follow semantic versioning.

