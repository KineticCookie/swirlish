import React from 'react';

import * as config from '../constants/config.js'

const ReactHighcharts = require('react-highcharts');

let io = require('socket.io-client')(config.SOCKET_IO_HOST);

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
                                    //console.log("data", point.x)
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
            </div>
        )
    }
}
