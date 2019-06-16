import React, {Component} from 'react';
import './styles.css';

export default class Fact extends Component {
  render() {
    return (
        <div>
            {this.props.bookName ?
                <div>
                    <h1 className="popular">Most Popular Book Right Now: <span>{this.props.bookName}</span></h1>
                    <h1 className="fact">Interesting fact about {this.props.bookName}:</h1>
                    <h2>{this.props.fact}</h2>
                </div> : ''}
        </div>
    );
  }
}
