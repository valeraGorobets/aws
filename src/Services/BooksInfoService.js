const BOOK_MAPPING = [{
    value: 'math',
    label: 'Math',
    authorWallet: '0x744cdf92e7b218ad8eC566870B602545e7278F77',
    feeAmount: 0.01,
    content: [{
      page: 1,
      hash: 'math1.jpg'
    },{
      page: 2,
      hash: 'math22.jpg'
    }],
  },
  {
    value: 'chemistry',
    label: 'Chemistry',
    authorWallet: '0x744cdf92e7b218ad8eC566870B602545e7278F77',
    feeAmount: 0.01,
    content: [{
      page: 1,
      hash: 'Chemistry1.jpg'
    },{
      page: 2,
      hash: 'Chemistry22.png'
    }],
  },
  {
    value: 'helloBook',
    label: 'Hello Book',
    authorWallet: '0x744cdf92e7b218ad8eC566870B602545e7278F77',
    feeAmount: 0.01,
    content: [{
      page: 1,
      hash: 'Hello book.png'
    },{
      page: 2,
      hash: 'FIRST LEARNING APPLICATION BASED ON SMART CONTRACTS.png'
    }],
  },
];

export function getBookMapping() {
  return BOOK_MAPPING;
}
