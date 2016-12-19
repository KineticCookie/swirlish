/**
 * Created by iskandar on 18/12/16.
 */

import React from 'react';

import * as config from '../../../constants/config.js'

const ReactHighcharts = require('react-highcharts');

export default class TimeSeriesGraphComponent extends React.Component {
    componentWillMount() {

    }

    render() {
        let job = this.props;

        return(
            <div>
                <ReactHighcharts config={{
                    chart: {
                        renderTo: this.props.uuid,
                        defaultSeriesType: 'spline',
                        events: {
                            load: function () {
                                let io = require('socket.io-client')(config.SOCKET_IO_HOST);

                                io.on(job.topic, function (point) {
                                    if (typeof this.series != 'undefined' && typeof this.series[0] != 'undefined') {
                                        let series = this.series[0];
                                        let shift = series.data.length > 20;
                                        this.series[0].addPoint(point, true, shift);
                                        //console.log(point)
                                    }
                                }.bind(this));
                            }
                        }
                    },
                    title: {
                        text: '#' + job.uuid + ' ' + job.node_name
                    },
                    xAxis: {
                        type: 'datetime',
                        tickPixelInterval: 100,
                        maxZoom: 2 * 1000
                    },
                    yAxis: {
                        minPadding: 0.2,
                        maxPadding: 0.2,
                        title: {
                            text: 'Value',
                            margin: 20
                        }
                    },
                    series: [{
                        name: 'Data',
                        data: []
                    }]
                }}/>
            </div>
        )
    }
}
