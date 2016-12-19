import { createStore, applyMiddleware, combineReducers, compose } from 'redux';
import thunk from 'redux-thunk';

// Add middleware to createStore
var createStoreWithMiddleware = compose(
    applyMiddleware(thunk),
    // add Redux devtools
    typeof window === 'object' && typeof window.devToolsExtension !== 'undefined' ? window.devToolsExtension() : f => f
)(createStore)

// App Reducers
import mainReducer from './reducers/main.js';
import testReducer from './reducers/test.js';
import jobsReducer from './reducers/jobs.js';

// Combine Reducers
var reducers = combineReducers({
    mainReducer: mainReducer,
    testReducer: testReducer,
    jobsReducer: jobsReducer
});

var rootReducer = (state, action) => {
    // state undefined sets store states to they initial values
    if (action.type === 'CLEAR_REDUCERS') state = undefined;

    return reducers(state, action);
}

// Create Store
var store = createStoreWithMiddleware(rootReducer);

export default store;
