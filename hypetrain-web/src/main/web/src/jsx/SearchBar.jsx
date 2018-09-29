import React from 'react';
import styled from 'styled-components';
import Service from './Service';
import {NavLink} from 'react-router-dom';
import SearchResult from './SearchResult'
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome'
import {faTimes} from '@fortawesome/free-solid-svg-icons'


const Div = styled.div`
  grid-area: search;
  text-align: center;
  max-width: 300px;
  margin: 0 auto;
`;

const InputContainer = styled.div`
  background-color: white;
  display: flex ;
  align-items: center;
  box-sizing: border-box;
  padding:5px;
`;

const ClearSearch = styled(FontAwesomeIcon)`
  color: ${props=> props.theme.colors.background}
  cursor: pointer;
`;

const Input = styled.input`
  border: none;
  width: 95%;
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

        this.state = {searchTimeout: null, searchResults: [], searchTerms:''};
    }

    /**
     * Search for a fighter by name/nickname
     * @param event
     */
    searchFighter(name) {

        clearTimeout(this.state.searchTimeout);
        this.setState({searchTerms: name},  () => {
            ;

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
        });
    }

    render() {
        return (
            <Div>
                <InputContainer>
                    <Input
                        type="text"
                        placeholder="Search for fighter"
                        autocomplete="off"
                        autocorrect="off"
                        autocapitalize="off"
                        spellcheck="false"
                        value={this.state.searchTerms}
                        onChange={(e) => this.searchFighter(e.target.value) }
                    />
                    {this.state.searchTerms.length > 0 && <ClearSearch icon={faTimes} onClick={()=> this.searchFighter('')}/>}
                </InputContainer>
                <Fighters>
                    {this.state.searchResults.map(f => {
                        return (
                            <FighterLink key={f.id} to={"/fighter/" + f.id}
                                         onClick={() => this.setState({searchResults: []})}>
                                <SearchResult fighter={f}/>
                            </FighterLink>
                        );
                    })}
                </Fighters>
            </Div>
        );
    }
}