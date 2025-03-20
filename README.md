# Playwright Framework for OpenCart Testing

A robust test automation framework built with Playwright, TestNG, and Maven for testing the OpenCart e-commerce platform.

## Features

- **Cross-browser Testing**: Support for Chrome, Firefox, and WebKit browsers
- **Page Object Model**: Well-structured page objects for better maintainability
- **Extent Reports**: Detailed HTML reports with screenshots and video recordings
- **Parallel Test Execution**: Configurable parallel test execution using TestNG
- **Video Recording**: Automatic video capture of test executions
- **Screenshot Capture**: Automatic screenshots on test failure and success
- **CI/CD Ready**: GitHub Actions workflow for automated testing

## Prerequisites

- Java JDK 17 or higher
- Maven 3.8 or higher
- Node.js 16 or higher (for Playwright)
- Git

## Project Structure

```
PlaywrightFramework/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/qa/opencart/
│   │           ├── factory/      # Playwright browser factory
│   │           ├── pages/        # Page objects
│   │           ├── api/          # API utilities
│   │           ├── listeners/    # Test listeners
│   │           └── utils/        # Utility classes
│   └── test/
│       ├── java/
│       │   └── com/qa/opencart/
│       │       └── tests/        # Test classes
│       └── resources/
│           ├── config/          # Configuration files
│           ├── testdata/        # Test data files
│           └── testrunners/     # TestNG XML files
├── reports/                     # Test execution reports
├── test-results/               # Test artifacts (videos, traces)
└── pom.xml                     # Maven configuration
```

## Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/PlaywrightFramework.git
   cd PlaywrightFramework
   ```

2. Install dependencies:
   ```bash
   mvn clean install
   ```

3. Install Playwright browsers:
   ```bash
   mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
   ```

## Running Tests

### Run all tests
```bash
mvn test
```

### Run specific test class
```bash
mvn test -Dtest=HomePageTests
```

### Run specific test method
```bash
mvn test -Dtest=HomePageTests#verifyHomePageTitleTest
```

### Run tests in parallel
```bash
mvn test -DthreadCount=3
```

## Test Reports

After test execution, you can find the reports in:
- HTML Report: `reports/TestExecutionReport.html`
- Test Results: `test-results/`
  - Videos: `test-results/videos/`
  - Traces: `test-results/trace/`

## CI/CD Integration

The framework includes a GitHub Actions workflow that:
1. Runs tests on multiple browsers
2. Generates and publishes test reports
3. Uploads test artifacts (videos, screenshots)

## Configuration

### Browser Configuration
Edit `src/test/resources/config/browsers.json` to configure browser settings:
```json
{
  "browsers": [
    {
      "name": "chrome",
      "headless": false,
      "slowMo": 50
    }
  ]
}
```

### Test Data
Test data can be configured in `src/test/resources/testdata/`

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- [Playwright](https://playwright.dev/)
- [TestNG](https://testng.org/)
- [Extent Reports](https://www.extentreports.com/)
- [OpenCart](https://www.opencart.com/) 



just adding some code to test
