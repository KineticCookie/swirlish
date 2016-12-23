export default function(state = { jobs: [], state: {} }, action) {
    switch (action.type) {
        case 'GET_JOBS_LIST':
            return Object.assign({}, state, { jobs: action.jobs });
        case 'CHANGE_JOB_CARD_STATE':
            return Object.assign({}, state, { state: action.state });
        default:
            return state;

    }
}
