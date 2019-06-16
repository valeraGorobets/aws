import React, {Component} from 'react';
import './styles.css';
import ContentView from "../ContentView";
import Chart from "../Chart";
import Fact from "../Fact";
import axios from 'axios';

const BACKEND_URL = 'http://localhost:5001';

export default class Main extends Component {
  state = {
    chart: {'Math': 0, 'Chemistry': 0, 'Hello Book': 0},
    loading: false,
    fact: '',
    bookName: '',
  };

  onSelected = async (selectedBook) => {
      const chartCopy = this.state.chart;
      chartCopy[selectedBook.label]++;
      this.setState({
          chart: chartCopy,
      });

      try {
          await axios.post(`${BACKEND_URL}/sendStatisticsToKafka`, {data: chartCopy});
          const fact = await axios.get(`${BACKEND_URL}/getFact`);
          this.setState({
              fact: fact.data.factAboutInterestingBook.value,
              bookName: fact.data.factAboutInterestingBook.key,
          });
      } catch (e) {
          console.log('Error in requests to spark: ' + e);
      }
  };

  render() {
    return (
      <div className="container">
        <div className="analyticsData">
            <div className="verticalAlign">
                <Fact fact={this.state.fact} bookName={this.state.bookName}/>
                <div className="chartWrapper">
                    {this.state.loading ?
                        <div className="loader-wrapper">
                            <div className="loader" />
                        </div>
                        : ''
                    }
                    <Chart chart = {this.state.chart}/>
                </div>
            </div>
            <ContentView clientWalletAddress = {this.props.clientWalletAddress} onBookSelected = {this.onSelected}/>
        </div>
      </div>
    );
  }
}
