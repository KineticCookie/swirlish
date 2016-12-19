/**
 * Created by iskandar on 19/12/16.
 */

import React from 'react';
import * as config from '../../../constants/config.js'

export default class DataGraphComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {data: {}};
    }

    componentWillMount() {
        let io = require('socket.io-client')(config.SOCKET_IO_HOST);
        io.on(this.props.topic, function (data) {
            this.setState({data: data});
        }.bind(this));
    }

    render() {
        let data = this.state.data;
        let keys = Object.keys(data);
        let elems = [];
        for (let i = 0; i<keys.length; i++) {
            elems.push(<div key={i}><b>{keys[i]}</b>: {data[keys[i]]}</div>)
        }

        return(
            <div className="panel panel-default">
                <div className="panel-body">
                    {elems}
                </div>
            </div>
        )
    }
}