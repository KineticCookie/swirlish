import React from 'react';

window.jQuery = require("jquery");
window.$ = require("jquery");

//require('../../../node_modules/bootstrap/dist/css/bootstrap.min.css');

export default class Base extends React.Component {
    render() {
        return(
            <div>
                {this.props.children}
            </div>
        )
    }
}
