/**
 * Created by iskandar on 18/12/16.
 */

import React from 'react';

import { connect } from 'react-redux';

import { getJobsList } from '../../api/jobs';

import JobCardComponent from './job-card';

const JobsListComponent = class JobsListComponent extends React.Component {
    componentWillMount() {
        getJobsList();
    }

    render() {
        let jobs = this.props.jobsReducer.jobs.map((elem, i) => {
           return(
               <JobCardComponent key={i} {...elem} />
           )
        });

        return(
            <div className="row">
                {jobs}
            </div>
        )
    }
}

const mapStateToProps = function(store) {
    return {
        jobsReducer: store.jobsReducer
    };
}

export default connect(mapStateToProps)(JobsListComponent);