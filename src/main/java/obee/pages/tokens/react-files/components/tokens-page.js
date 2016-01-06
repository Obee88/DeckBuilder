"use strict";

import React from 'react';
import TokensBoard from './tokens-board.js';
import PrintPaper from './print-paper.js';

class TokensPage extends React.Component {

	constructor(){
		super();
		this.state = {
			page:[],
			tokens:[],
			filter: "",
			selectedTokens: []
		};
		this.refreshTokens("");
	}

	onFilterChange(e){
		this.refreshTokens(e.target.value);
	}

	refreshTokens(filter){
		// var url = "http://localhost:8081/DeckBuilderApi/tokens?filter="+filter;
		var url = "http://185.53.129.19:8080/server-rest/DeckBuilderApi/tokens?filter="+filter;
		var self = this;
		var onTokensRecived = function(tokens){
			self.setState({tokens:tokens, "filter":filter});
		};
		$.ajax({
			url: url,
			dataType: "jsonp",
			jsonpCallback: "onTokensRecived",
			success: onTokensRecived
		});
	}

	makeTokenSelection(model){
		var oldList = this.state.selectedTokens;
		if (oldList.length<18){
			oldList.push(model);
			this.setState({selectedTokens: oldList});
		}
	}

	makeTokenDeletion(model){
		var array = this.state.selectedTokens;
		var index = array.indexOf(model);
		if (index > -1) {
		    array.splice(index, 1);
		}
		this.setState({selectedTokens: array});
	}

	doPrintRequest(){
		var arr = [];
		this.state.selectedTokens.forEach(function(model){
			arr.push(model["id"]);
		});
		// var url = "http://localhost:8081/DeckBuilderApi/tokens/print?arr="+arr.toString();
		var url = "http://185.53.129.19:8080/server-rest/DeckBuilderApi/tokens/print?arr="+arr.toString();
		var win = window.open(url);
  		win.focus();
	}

	render(){
		return (
			<table>
				<tr>
					<td>
						<table>
							<tr>
								<td>
									<input value={this.state.filter} onChange={this.onFilterChange.bind(this)} />{" : " + this.state.tokens.length}
								</td>
								<td>
									<PrintPaper model = {this.state.selectedTokens} tokenDeletionCallback={this.makeTokenDeletion.bind(this)}/>
								</td>
								<td>
									<button onClick={this.doPrintRequest.bind(this)} >print this</button>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td>
						<table>
							<tr>
								<td>
									<TokensBoard tokens={this.state.tokens} selectedToken={this.state.selectedToken} tokenSelectionCallback={this.makeTokenSelection.bind(this)} />
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>	
		);
	}
}

function render(dummy) {
	React.render(
		<TokensPage />,
		document.getElementById('tokens-content')
	);
}

document.addEventListener('DOMContentLoaded', function () {
	render();
});