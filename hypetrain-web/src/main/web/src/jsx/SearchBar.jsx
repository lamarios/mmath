import React from 'react';
import styled from 'styled-components';
import Service from './Service';
import {NavLink} from 'react-router-dom';


const Div = styled.div`
  grid-area: search;
  text-align: center;
`;

const InputContainer = styled.div`
  background-color: white;
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
const Fighters = styled.div`
  
`;

const FighterLink = styled(NavLink)`
  display:block;
  text-decoration: none;
  
`;

export default class SearchBar extends React.Component {

    constructor(props) {
        super(props);

        this.searchFighter = this.searchFighter.bind(this);
        this.service = new Service();

        this.state = {searchTimeout: null, searchResults: []};
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
                () => this.service.searchFighter(name).then(res => this.setState({searchResults: res}))
                , 300
            );
            this.setState({
                searchTimeout: timeout
            });
        } else {
            this.setState({searchResults: []});
        }
    }

    render() {
        return (
            <Div>
                <InputContainer>
                    <Input
                        type="text"
                        placeholder="Search for fighter"
                        onKeyUp={e => this.searchFighter(e)}
                        autocomplete="off"
                        autocorrect="off"
                        autocapitalize="off"
                        spellcheck="false"
                    />
                </InputContainer>
                <Fighters>
                    {this.state.searchResults.map(f => {
                        return (<FighterLink key={f.id} to={"/fighter/" + f.id}
                                             onClick={() => this.setState({searchResults: []})}>{f.name}</FighterLink>);
                    })}
                </Fighters>
            </Div>
        );
    }
}