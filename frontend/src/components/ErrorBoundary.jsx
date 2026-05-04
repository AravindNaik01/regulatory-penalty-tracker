import React from 'react';

class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null, errorInfo: null };
  }

  static getDerivedStateFromError(error) {
    // Update state so the next render will show the fallback UI.
    return { hasError: true };
  }

  componentDidCatch(error, errorInfo) {
    this.setState({
      error: error,
      errorInfo: errorInfo
    });
    // Here you would typically log the error to an error reporting service
    console.error("ErrorBoundary caught an error:", error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      // Fallback custom UI
      return (
        <div style={{ padding: '24px', backgroundColor: '#fee2e2', color: '#991b1b', borderRadius: '8px', margin: '20px 0', fontFamily: 'sans-serif' }}>
          <h2 style={{ marginTop: 0 }}>Something went wrong.</h2>
          <p>The application encountered an unexpected error. Please try refreshing the page.</p>
          <details style={{ whiteSpace: 'pre-wrap', marginTop: '16px', fontSize: '14px', background: 'rgba(255,255,255,0.5)', padding: '12px', borderRadius: '4px' }}>
            {this.state.error && this.state.error.toString()}
            <br />
            {this.state.errorInfo && this.state.errorInfo.componentStack}
          </details>
        </div>
      );
    }

    return this.props.children;
  }
}

export default ErrorBoundary;
