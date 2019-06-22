import React from 'react';
import styled, {ThemeProvider} from 'styled-components';
import {theme} from './theme';

import {BrowserRouter, Route} from 'react-router-dom';

import Title from './Title';
import SearchBar from './SearchBar';
import Session from "./Session";
import TopTrains from "./TopTrains";
import Fighter from './Fighter';
import Me from "./Me";

import background from "../images/background.jpeg";

const Background = styled.div`
      background-color: ${props => props.theme.colors.background};
      background-image: url("${background}");
      background-size: cover;
      background-repeat: no-repeat;
      min-height: 100vh;
`;
const Content = styled.div`
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
  padding:10px;
 
    max-width: 500px; 
    margin:0 auto;
    overflow-x: hidden;
`

export default class App extends React.Component {


    render() {
        return (<ThemeProvider theme={theme}>
            <BrowserRouter>
                <Background>
                    <Content>
                        <Session/>
                        <Title/>
                        <SearchBar/>
                        <Route exact path="/" component={TopTrains}/>
                        <Route exact path="/me" component={Me}/>
                        <Route exact path="/fighter/:fighter" component={Fighter}/>
                    </Content>
                </Background>
            </BrowserRouter>
        </ThemeProvider>);
    }

}