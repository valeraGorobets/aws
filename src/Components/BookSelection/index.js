import React, {Component} from 'react';
import './styles.css'
import Select from 'react-select';
import { getBookMapping } from "../../Services/BooksInfoService";

export default class BookSelection extends Component {
  state = {
    bookMapping: [],
    selectedBookOption: null,
    selectedPageOption: null,
    bookOptions: [],
    pageOptions: [],
  };

  componentDidMount() {
    const bookMapping = getBookMapping();
    this.setState({
      bookOptions: bookMapping.map(book => {
        const {value, label} = book;
        return {value, label};
      }),
      pageOptions: [],
      bookMapping
    })
  }

  extractPageOptions = (bookId) => {
    return this.state.bookMapping
        .find(book => book.value === bookId)
        .content.map(item => {
          return {
            label: item.page.toString(),
            value: item.page,
          }
        });
  };

  handleBookChange = (selectedBookOption) => {
    this.setState({
      selectedBookOption,
      pageOptions: this.extractPageOptions(selectedBookOption.value),
      selectedPageOption: null,
    });
  };

  handlePageChange = (selectedPageOption) => {
    this.setState({ selectedPageOption });
  };
  onSubmitClick = () => {
    let selectedBook = this.state.bookMapping
        .find(book => book.value === this.state.selectedBookOption.value);
    const hash = selectedBook
        .content
        .find(item => item.page === this.state.selectedPageOption.value)
        .hash;

    this.props.onBookSelected(hash, selectedBook);
  };

  getBookOptions = () => this.state.bookOptions;
  getPageOptions = () => this.state.pageOptions;

  render() {
    const { selectedBookOption,  selectedPageOption} = this.state;
    return (
        <div>
            <h2>Select Book And Page:</h2>
            <div className="wrapper">
                <Select
                    value={selectedBookOption}
                    onChange={this.handleBookChange}
                    options={this.getBookOptions()}
                />
                <Select
                    value={selectedPageOption}
                    onChange={this.handlePageChange}
                    options={this.getPageOptions()}
                />
                <button onClick={this.onSubmitClick} disabled={!this.state.selectedBookOption || !this.state.selectedPageOption}>Go</button>
            </div>
        </div>
    );
  }
}
