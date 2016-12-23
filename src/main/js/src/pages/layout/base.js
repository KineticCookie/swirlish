import React from 'react';

window.jQuery = require("jquery");
window.$ = require("jquery");

require('../../../node_modules/bootstrap/dist/css/bootstrap.min.css');
require('../../../node_modules/bootstrap/dist/js/bootstrap.min');

export default class Base extends React.Component {
    render() {
        return(
            <div className="container">
                {this.props.children}
            </div>
        )
    }
}
