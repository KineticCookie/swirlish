import React from 'react';

import SocketTest from '../../components/socket-test.js';
import MQTTTest from '../../components/mqtt-test';

export default class Main extends React.Component {
    render() {
        return(
            <div>
                <SocketTest/>
                <MQTTTest/>
            </div>
        )
    }
}
