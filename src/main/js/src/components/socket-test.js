import React from 'react';

const ReactHighcharts = require('react-highcharts');

let io = require('socket.io-client')("http://0.0.0.0:8000");

export default class SocketTest extends React.Component {
    componentDidMount() {
        io.on('connect', function(){
            console.log("connect")
        });
        io.on('disconnect', function(){
            console.log("disconnect")
        });
        setInterval(function () {
            io.emit('test node', { some: "data" })
        }, 1000);
    }


    handleClick() {
        io.emit('test node', { some: "data" })
    }

    render() {
        return (
            <div>
                <ReactHighcharts config={{
                    chart: {
                        renderTo: '#chart',
                        defaultSeriesType: 'spline',
                        events: {
                            load: function () {
                                io.on('node', function (point) {
                                    let series = this.series[0];
                                    let shift = series.data.length > 20;
                                    this.series[0].addPoint(point, true, shift);
                                    console.log("data", point.x)
                                }.bind(this));
                            }
                        }
                    },
                    title: {
                        text: 'Live random data'
                    },
                    xAxis: {
                        type: 'datetime',
                        tickPixelInterval: 150,
                        maxZoom: 20 * 1000
                    },
                    yAxis: {
                        minPadding: 0.2,
                        maxPadding: 0.2,
                        title: {
                            text: 'Value',
                            margin: 80
                        }
                    },
                    series: [{
                        name: 'Random data',
                        data: []
                    }]
                }}/>
                <button onClick={this.handleClick.bind(this)}>Button</button>
            </div>
        )
    }
}
