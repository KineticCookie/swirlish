/**
 * Created by iskandar on 18/12/16.
 */

import React from 'react';

import JobsListComponent from '../../components/jobs/jobs-list';
import JobStateComponent from '../../components/jobs/job-state';

export default class JobsList extends React.Component {
    render() {
        return(
            <div>
                <div className="col-md-6">
                    <h3>Jobs list</h3>
                    <JobsListComponent/>
                </div>
                <div className="col-md-6">
                    <h3>State</h3>
                    <JobStateComponent />
                </div>
            </div>
        )
    }
}