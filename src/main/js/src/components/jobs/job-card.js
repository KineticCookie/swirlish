/**
 * Created by iskandar on 18/12/16.
 */

import React from 'react';
import store from '../../store.js';
import JobCardModalComponent from './job-card-modal';
import TimeSeriesGraphComponent from './graph/timeseries';
import {changeJobCardState} from '../../actions/jobs.js';

export default class JobCardComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {selected: false};
    }

    selectCard() {
        this.state.selected ? this.setState({selected: false}) : this.setState({selected: true});
        setTimeout(function() {
            this.state.selected ? this.setState({selected: false}) : this.setState({selected: true});
        }.bind(this), 500);
        store.dispatch(changeJobCardState(this.props));
    }

    render() {
        let job = this.props;

        return(
            <div className="col-md-4" onClick={this.selectCard.bind(this)}>
                <div className={this.state.selected ? "panel panel-primary": "panel panel-default"}>
                    <div className="panel-body">
                        <b>ID</b>: {job.uuid} <br/>
                        <b>Node name</b>: {job.node_name} <br/>
                        <b>Description</b>: {job.description} <br/>
                        <b>Cluster name</b>: {job.cluster_name}
                    </div>
                </div>
            </div>
        )
    }
}