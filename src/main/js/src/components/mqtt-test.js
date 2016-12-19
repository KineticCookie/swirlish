/**
 * Created by iskandar on 17/12/16.
 */
import React from 'react';


import * as config from '../constants/config.js'

export default class MQTTTest extends React.Component {
    componentDidMount() {
        let mqtt = require('mqtt');
        let io = require('socket.io-client')(config.SOCKET_IO_HOST);

        io.on('connect', function() {
            let client  = mqtt.connect('mqtt://test.mosquitto.org');

            client.on('connect', function () {
                console.log("connected to mqtt");
                client.subscribe('crimethory');
                socket.emit('connected');
            });
        });
    }

    render() {
        return(
            <div>
                MQTT: TODO
            </div>
        )
    }
}