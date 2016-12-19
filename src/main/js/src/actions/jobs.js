/**
 * Created by iskandar on 18/12/16.
 */

import * as types from '../constants/types';

export function jobsListAction(jobs) {
    return {
        type: types.GET_JOBS_LIST,
        jobs: jobs
    }
}

export function changeJobCardState(state) {
    return {
        type: types.CHANGE_JOB_CARD_STATE,
        state: state
    }
}