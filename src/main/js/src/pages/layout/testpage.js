import React from 'react';
import { connect } from 'react-redux';

import { testConnection } from '../../api/test.js';
import store from '../../store.js';

const TestPage =  class Test extends React.Component {
    componentWillMount() {
        testConnection();
    }

    render() {
        let testData = this.props.testReducer.data.map((elem, i) => {
            return(
                <li key={i}>{elem.first_name} {elem.last_name} {elem.email}</li>
            )
        });

        return(
            <ul>{testData}</ul>
        )
    }
}

const mapStateToProps = function(store) {
    return {
        testReducer: store.testReducer
    };
}

export default connect(mapStateToProps)(TestPage);
