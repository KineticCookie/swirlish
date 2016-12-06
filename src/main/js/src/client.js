import React, { PropTypes } from 'react'
import { render } from 'react-dom';
import { Router, Route, IndexRoute, browserHistory, hashHistory } from 'react-router'
import { Provider } from 'react-redux';
import store from './store.js';

// base layout page
import Base from './pages/layout/Base.js';
// main page
import Main from './pages/layout/Main.js';
// 404 page
import NotFound from './pages/error/NotFound.js';

import Test from './pages/layout/testpage.js';

render((
    <Provider store={store}>
        <Router history={browserHistory}>
            <Route path="/" component={Base}>
                <IndexRoute component={Main} />
                <Route path="/test" component={Test} />
                <Route path="*" component={NotFound} />
            </Route>
        </Router>
    </Provider>
), document.getElementById('root'))
