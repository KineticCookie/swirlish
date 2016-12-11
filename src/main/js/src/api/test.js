import axios from 'axios';

import store from '../store.js';

export function testConnection() {
    axios.get(
        'src/api/mock/data.json'
    ).then((response) => {
        store.dispatch({
            type: 'GET_TEST_DATA',
            testData: response.data
        });
    }).catch((err) => {
        console.log(err);
    });
}
