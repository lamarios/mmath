import React from 'react';
import styled, {ThemeProvider} from 'styled-components';
import {theme} from './theme';


import Title from './Title';
import SearchBar from './SearchBar';
import Session from "./Session";
import TopTrains from "./TopTrains";

const Content = styled.div`
  background-color: ${props => props.theme.colors.background};
  color: ${props => props.theme.colors.text};
  
  font-family: 'Open Sans', sans-serif;
  display: grid;
  grid-template-areas: 
    'session'
    'title'
    'search'
    'content'
    'footer'
  ;

`

export default class App extends React.Component {


    render() {
        return (<ThemeProvider theme={theme}>
            <Content>
                <Session/>
                <Title/>
                <SearchBar/>
                <TopTrains/>
            </Content>
        </ThemeProvider>);
    }

}