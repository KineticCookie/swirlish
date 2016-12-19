/**
 * Created by iskandar on 19/12/16.
 */

import React from 'react';
import { connect } from 'react-redux';

import * as consts from '../../constants/consts';

import TimeSeriesGraphComponent from './graph/timeseries';
import DataGraphComponent from './graph/data';
import StackedGraphComponent from './graph/stacked';

const JobStateComponent = class JobStateComponent extends React.Component {
    render() {
        let jr = this.props.jobsReducer;
        let filled = Object.getOwnPropertyNames(jr.state).length > 0;

        let graph = "";

        if (filled && typeof jr.state.node_type != 'undefined') {
            switch (jr.state.node_type) {
                case consts.GRAPH_TYPE_DATA:
                    graph = <DataGraphComponent {...jr.state}/>;
                    break;
                case consts.GRAPH_TYPE_STACKED:
                    graph = <StackedGraphComponent {...jr.state}/>;
                    break;
                case consts.GRAPH_TYPE_TIMESERIES:
                    graph = <TimeSeriesGraphComponent {...jr.state}/>;
                    break;
                default:
                    graph = <DataGraphComponent {...jr.state}/>;
            }
        }

        return(
            <div>
                {filled ? graph : "Select job"}
            </div>
        )
    }
}

const mapStateToProps = function(store) {
    return {
        jobsReducer: store.jobsReducer
    };
}

export default connect(mapStateToProps)(JobStateComponent);