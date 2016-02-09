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
    return <table>
      <tbody>
        <tr>
          <td>Sunrise time</td>
          <td>{this.state.sunrise}</td>
        </tr>
        <tr>
          <td>Sunset time</td>
          <td>{this.state.sunset}</td>
        </tr>
        <tr>
          <td>Current temperature</td>
          <td>{this.state.temperature}</td>
        </tr>
        <tr>
          <td>Requests</td>
          <td>{this.state.requests}</td>
        </tr>
      </tbody>
    </table>
  }
});
