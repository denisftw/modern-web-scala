import React from 'react';

module.exports = React.createClass({
  getInitialState: function() {
    return {
      sunrise: undefined,
      sunset: undefined,
      temperature: undefined,
      requests: undefined
    }
  },
  componentDidMount: function() {
    let reactRef = this;
    fetch('/data').then(function(response) {
      return response.json();
    }).then(function(json) {
      reactRef.setState({
        sunrise: json.sunInfo.sunrise,
        sunset: json.sunInfo.sunset,
        temperature: json.temperature,
        requests: json.requests
      });
    });
  },
  render: function() {
    return <div>
      <div>Sunrise time: {this.state.sunrise}</div>
      <div>Sunset time: {this.state.sunset}</div>
      <div>Current temperature: {this.state.temperature}</div>
      <div>Requests: {this.state.requests}</div>
    </div>
  }
});
