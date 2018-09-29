import React from 'react';
import styled from 'styled-components';


const Result = styled.div`
    background-color: ${props => props.theme.colors.text};
    color: ${props => props.theme.colors.background};
    padding:5px;
`;

export default class SearchResult extends React.Component{

    render(){
        return (<Result>
            {this.props.fighter.name}
        </Result>);
    }
}