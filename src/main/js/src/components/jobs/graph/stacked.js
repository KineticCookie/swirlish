/**
 * Created by iskandar on 19/12/16.
 */

import React from 'react';

import * as config from '../../../constants/config.js'

const ReactHighcharts = require('react-highcharts');

export default class StackedGraphComponent extends React.Component {
    componentWillMount() {

    }

    render() {
        return(
            <div>
                <ReactHighcharts config={{
                    chart: {
                        type: 'bar',
                        events: {
                            load: function () {
                                let io = require('socket.io-client')(config.SOCKET_IO_HOST);

                                io.on('stacked', function (data) {
                                    if (typeof this.series != 'undefined') {
                                        {/*for (let i = 0; i < data.length; i++) {*/}
                                            {/*let container = Object.assign({}, this.series[0]);*/}
                                            {/*container.data = data[i];*/}
                                            {/*//this.series.push(container)*/}
                                        {/*}*/}

                                    }
                                    console.log(typeof this.series[0]);
                                }.bind(this));
                            }
                        }
                    },
                    title: {
                        text: 'Stacked bar chart'
                    },
                    xAxis: {
                        categories: ['Apples']
                    },
                    yAxis: {
                        min: 0,
                        title: {
                            text: 'Total fruit consumption'
                        }
                    },
                    legend: {
                        reversed: true
                    },
                    plotOptions: {
                        series: {
                            stacking: 'normal'
                        }
                    },
                    series: [{
                        name: 'empty',
                        data: []
                    }]
                }}/>
            </div>
        )
    }
}
