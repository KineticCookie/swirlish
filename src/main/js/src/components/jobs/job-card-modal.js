/**
 * Created by iskandar on 18/12/16.
 */

import React from 'react';

import TimeSeriesGraphComponent from './graph/timeseries';

export default class JobCardModalComponent extends React.Component {
    render() {
        let data = this.props;

        console.log($("#" + data.anchor).hasClass('in'))

        return(
            <div className="modal fade" id={data.anchor} tabIndex="-1" role="dialog" aria-labelledby="myModalLabel">
                <div className="modal-dialog modal-lg" role="document">
                    <div className="modal-content">
                        <div className="modal-header">
                            <button type="button" className="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                            <h4 className="modal-title" id="myModalLabel">{data.node_name}</h4>
                        </div>
                        <div className="modal-body">
                            {data.collapsed ? "" : <TimeSeriesGraphComponent {...data} />}

                        </div>
                        <div className="modal-footer">
                            <button type="button" className="btn btn-default" data-dismiss="modal">Close</button>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}