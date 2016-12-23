/**
 * Created by iskandar on 18/12/16.
 */

import axios from 'axios';

import { jobsListAction } from '../actions/jobs';

import store from '../store.js';


export function getJobsList() {
    axios.get('src/api/mock/jobs/list.json').then((response) => {
        let jobs = response.data;
        store.dispatch(jobsListAction(jobs))
    }).catch((err) => {
        console.log(err)
    })
}