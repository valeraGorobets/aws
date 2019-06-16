import React, {Component} from 'react';
import './styles.css'
import BookSelection from "../BookSelection";

export default class ContentView extends Component {
  state = {
    srcHash: '',
  };
  onSelected = (srcHash, selectedBook) => {
    if (srcHash !== this.state.srcHash) {
        this.setState({srcHash});
        this.props.onBookSelected(selectedBook);
    }
  };
  render() {
    return (
        <div className="contentView">
            <BookSelection clientWalletAddress = {this.props.clientWalletAddress} onBookSelected = {this.onSelected}/>
            <div className="bookView">
                {this.state.srcHash ?
                    <img src={"/" + this.state.srcHash} alt=""/>
                     : <h2>Not selected</h2>
                }
            </div>
        </div>
    );
  }
}
