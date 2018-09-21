import React from 'react';
import styled from 'styled-components';
import Service from './Service';

const Div = styled.div`
  grid-area: search;
  text-align: center;
`;

const InputContainer = styled.div`
  background-color: white;
  width: 300px;
  display: inline-block;
  box-sizing: border-box;
  padding:5px;
`;

const Input = styled.input`
  border: none;
  width: 100%;
  box-sizing: border-box;
  font-size:20px;
`;

export default class SearchBar extends React.Component {

    constructor(props) {
        super(props);

        this.searchFighter = this.searchFighter.bind(this);
        this.service = new Service();

        this.state = {searchTimeout: null};
    }

    /**
     * Search for a fighter by name/nickname
     * @param event
     */
    searchFighter(event) {

        clearTimeout(this.state.searchTimeout);
        const name = event.target.value;

        if (typeof name !== 'undefined' && name.length > 0) {
            let timeout = setTimeout(
                () => this.service.searchFighter(name).then(res => console.log(res))
                , 300
            );
            this.setState({
                searchTimeout: timeout
            });
        }
    }

    render() {
        return (
            <Div>
                <InputContainer>
                    <Input type="text" placeholder="Search for fighter" onKeyUp={e => this.searchFighter(e)}/>
                </InputContainer>
            </Div>
        );
    }
}