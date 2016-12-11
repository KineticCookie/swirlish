export default function(state = { data: [] }, action) {
    switch (action.type) {
        case 'GET_TEST_DATA':
            return Object.assign({}, state, { data: action.testData });
        default:
            return state;

    }
}
