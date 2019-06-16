import React, { Component } from 'react';
import Header from './Components/Header';
import Main from './Components/Main';
import Footer from './Components/Footer';

import './styles.css';

class App extends Component {
  state = {
    clientWalletAddress: '0xc9E4C7e8E2C9055492E3971dD380a359f4Ec4111',
  };

  render() {
    return (
      <div>
        <div className="content">
          <Header clientWalletAddress = {this.state.clientWalletAddress}/>
          <Main clientWalletAddress = {this.state.clientWalletAddress}/>
        </div>
        <Footer />
      </div>
    );
  }
}

export default App;
      