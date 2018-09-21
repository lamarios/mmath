import React from 'react';
import styled from 'styled-components';
import Service from './Service';

const Div = styled.div`
  grid-area: search;
`


const Input = styled.input`
  border: none;
`

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
                <Input type="text" placeholder="Search for fighter" onKeyUp={e => this.searchFighter(e)}/>
            </Div>
        );
    }
}