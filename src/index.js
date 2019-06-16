import React from 'react';
import ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import { createStore } from 'redux';
import { BrowserRouter } from 'react-router-dom';
import helloBookApp from './Reducers';
import App from './App';

let store = createStore(helloBookApp);

ReactDOM.render(
  <Provider store={store}>
  	<BrowserRouter>
      <App />
  	</BrowserRouter>
  </Provider>,
  document.getElementById('root'));