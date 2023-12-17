 // Define mapping of user-friendly column names to actual column names
		    let columnMapping = {
		        'Azonosító': 'identifier',
		        'I_km/h': 'speed1',
		        'II_km/h': 'speed2',
		        'III_km/h': 'speed3',
		        'I_ak_kat_N': 'acousticCatDay1',
		        'II_ak_kat_N': 'acousticCatDay2',
		        'III_ak_kat_N': 'acousticCatDay3',
		        'I_ak_kat_E': 'acousticCatNight1',
		        'II_ak_kat_E': 'acousticCatNight2',
		        'III_ak_kat_E': 'acousticCatNight3',
		        'R_Azonosító': 'identifierR',
		        'R_I_km/h': 'speed1R',
		        'R_II_km/h': 'speed2R',
		        'R_III_km/h': 'speed3R',
		        'R_I_ak_kat_N': 'acousticCatDayR1',
		        'R_II_ak_kat_N': 'acousticCatDayR2',
		        'R_III_ak_kat_N': 'acousticCatDayR3',
		        'R_I_ak_kat_E': 'acousticCatNightR1',
		        'R_II_ak_kat_E': 'acousticCatNightR2',
		        'R_III_ak_kat_E': 'acousticCatNightR3',
		        'LAeq Nappal' : 'laeqDay',
		        'LAeq Éjjel' : 'laeqNight',
		        'LW Nappal' : 'lwDay',
		        'LW Éjjel' : 'lwNight',
		        'Védőtávolság Nappal' : 'protectiveDistanceDay',
		        'Védőtávolság Éjjel' : 'protectiveDistanceNight',
		        'Hatásterület Nappal' : 'impactAreaDay',
		        'Hatásterület Éjjel' : 'impactAreaNight',
		        'Zajterhelés x távolságon Nappal' : 'noiseAtGivenDistanceDay',
		        'Zajterhelés x távolságon Éjjel' : 'noiseAtGivenDistanceNight',
		    };
		    
		    const editableColumns = [
		        'identifier',
		        'speed1',
		        'speed2',
		        'speed3',
		        'acousticCatDay1',
		        'acousticCatDay2',
		        'acousticCatDay3',
		        'acousticCatNight1',
		        'acousticCatNight2',
		        'acousticCatNight3',
		        'identifierR',
		        'speed1R',
		        'speed2R',
		        'speed3R',
		        'acousticCatDayR1',
		        'acousticCatDayR2',
		        'acousticCatDayR3',
		        'acousticCatNightR1',
		        'acousticCatNightR2',
		        'acousticCatNightR3',
	        ];
		    
		    let differenceColumnDefault = [0, 1, 2];
		    let differenceColumnTemp = [];
		    let differenceColumnToUpdate = 0;
		    
		    let previousDifferenceColumnToUpdate;
			let previousDifferenceColumnTemp;
			let previousDifferenceColumnDefault;
			
			// after modifications let the calculations run again
			let isModificationHappened = false;

			// handle data modifications
			let isCellEditing = false;		
					
			const handleCellClick = (e) => {
			    const cell = e.target.closest('td');
			    if (!cell) {
			        return;
			    }
			    
			    if (isCellEditing) {
			        return; // Prevents re-triggering when the cell is already in edit mode
			    }
			
			    isCellEditing = true;
			
			    const activeData = groupedData[activeFileId];
			    const row = cell.parentNode.rowIndex -1;
			    const column = cell.cellIndex;
			
			    const columnNames = Object.values(columnMapping);
			    const columnName = columnNames[column];
			    const cellValue = activeData[row][columnName];
			
				console.log('activeData', activeData);
				console.log('row', row);
				console.log('columnName', columnName);
			    console.log('cellValue in data-modification:', cellValue);
			
			    if (editableColumns.includes(columnName)) {			
			        const input = document.createElement('input');
			        input.value = cellValue;
			        
	        		input.addEventListener('input', (event) => {
					    const newValue = event.target.value.trim();
					    if (newValue === '') {
					        // If the new value is empty, revert to the original value
					        input.value = cellValue; // Revert the input value
					    } else if (/^\d*$/.test(newValue)) {
					        // Only allow integer values
					        input.value = newValue; // Update input value
					    } else {
					        // Prevent non-integer values from being entered
					        input.value = ''; // Reset input value
					    }
					});
			        
			        cell.innerHTML = '';
			        cell.appendChild(input);
			        
			        input.focus(); 
			        input.select(); 
			
			        input.addEventListener('blur', () => {
					    const updatedValue = input.value;
					    
					    console.log('Updated Value:', updatedValue);
					    console.log('Cell Value:', cellValue);
					    console.log('Are they different?', String(updatedValue) !== String(cellValue));
					
					    // Check if the value has changed
					    if (String(updatedValue) !== String(cellValue)) {
					        activeData[row][columnName] = updatedValue;
					        cell.innerHTML = updatedValue;
					
					        console.log('activeFileId, row, columnName, value', activeFileId, row, columnName, updatedValue);
					        console.log('activeData in data-modification:', activeData);
					
					        saveCellDataToBackend(activeFileId, row, columnName, updatedValue);
					    } else {
					        // Revert the cell content to the original value when unchanged
					        cell.innerHTML = cellValue;
					    }
					
					    isCellEditing = false; // Reset the editing flag
					});
			        
			        input.addEventListener('keydown', (event) => {
			            if (event.key === 'Enter') {
			                input.blur();
			            }
			        });
			    } else {
			        console.log('Cell not editable');
			        isCellEditing = false; 
			    }
			};
			
			// Function to send updated data to the backend
			const saveCellDataToBackend = (activeFileId, row, columnName, updatedCellValue) => {
			    // Make a POST request to the backend API to save the updated data
			    fetch(`/modification/cellValue/${activeFileId}/${row}/${columnName}/${updatedCellValue}`, {
			        method: 'PUT',
			        headers: {
			            'Content-Type': 'application/json',
			        },
			    })
			    .then(response => {
			        if (!response.ok) {
			            throw new Error('Failed to save data to the backend');
			        }
			        const activeData = groupedData[activeFileId];
			        console.log('activeData in saveCellDataToBackend, RESPONSE OK', activeData);
			        isModificationHappened = true;
			    })
			    .catch(error => {
			        console.error('Error while updating data:', error);
			        // Handle error or display error message
			    });
			};


			// handle sorting request
			function handleSorting() {
				console.log('groupedData in sorting:', groupedData);
				if (groupedData[activeFileId][0].laeqDay !== null && groupedData[activeFileId][0].laeqNight !== null) {
					fetch(`/sortAndDifferences/sortByLaeq/${activeFileId}`, {
				        headers: {
				            'Content-Type': 'application/json'
				        },
				    })
				    .then(response => {
						if (!response.ok) {
							throw new Error('Network response was not ok');
						}
						return response.json();
					})
				    .then(data => {
				        console.log('Fetched Data after Sorting:', data);
				        renderTable(data);
				    })
				    .catch(error => {
				        console.error('There was a problem with the fetch operation:', error);
				    });
				}
			}

			// handle difference request
			function handleDifferenceSubmit() {
				console.log('groupedData in difference:', groupedData);
			    const files = Object.keys(groupedData); // Assuming groupedData contains uploaded file names
			
			    // Only proceed if at least 2 files are available
			    if (files.length >= 2) {
			        const container = document.getElementById('difference-container');
			
			        // Create dropdown elements
			        const dropdown1 = document.createElement('select');
			        const dropdown2 = document.createElement('select');
			
			        // Fill dropdowns with file names
			        files.forEach(fileId => {
					    const fileName = groupedData[fileId][0].fileName; // Get the fileName based on the fileId

					    const option2 = document.createElement('option');
					
					    // Use fileName for display and fileId for value
					    option2.text = fileName;
					    option2.value = fileId;
					
					   // dropdown1.appendChild(option1);
					    dropdown2.appendChild(option2);
					});
					
					// Get the active file's fileName using activeFileId
			        const activeFileName = groupedData[activeFileId][0].fileName;
			
			        // Create an unchangeable dropdown 1 for the active file's fileName
			        const option1 = document.createElement('option');
			        option1.text = activeFileName;
			        option1.value = activeFileId;
			        option1.disabled = true;
			        option1.selected = true;
			
			        dropdown1.appendChild(option1);
			
			        // Create OK button
			        const okButton = document.createElement('button');
			        okButton.textContent = 'OK';
			        okButton.onclick = () => {
			            const fileId1 = dropdown1.value; // Get the selected fileId from the dropdown
    					const fileId2 = dropdown2.value; 
			            
			            const file1 = groupedData[fileId1];
					    const file2 = groupedData[fileId2];
					
					    // Function to check if laeqDay and laeqNight are null
					    const checkLaeqNull = (file) => {
					        return file.some(entry => entry.laeqDay === null || entry.laeqNight === null);
					    };
					
					    // Check if laeqDay or laeqNight are null in either file
					    if (checkLaeqNull(file1) || checkLaeqNull(file2)) {
					        console.log('Error: laeqDay or laeqNight is null in one of the selected files.');
					        return; // Prevent sending data if any null value is found
					    }
						// create new columns for the values
						createNewColumnsForDifferences(fileId1, fileId2);
         
			            // Remove the dropdownDiv when 'OK' is clicked
		                if (dropdownDiv && dropdownDiv.parentNode) {
		                    dropdownDiv.parentNode.removeChild(dropdownDiv);
		                    dropdownDiv = null; // Reset dropdownDiv
		                }
			        };
			
			        // Create a div to hold dropdowns and the OK button
			        let dropdownDiv = document.createElement('div');
			        dropdownDiv.appendChild(dropdown1);
			        dropdownDiv.appendChild(document.createTextNode(' és '));
			        dropdownDiv.appendChild(dropdown2);
			        dropdownDiv.appendChild(document.createTextNode('különbsége'));
			        dropdownDiv.appendChild(okButton);
			
			        // Append the div to the container
			        container.appendChild(dropdownDiv);
			    } else {
			        console.log('At least two files are required for comparison.');
			    }
			}
			
			function createNewColumnsForDifferences(fileId1, fileId2) {
				let fileName1 = groupedData[fileId1][0].fileName;
				let fileName2 = groupedData[fileId2][0].fileName;
				
				if (differenceColumnTemp.length < 3) {
					let newKeyDay = `${fileName1} - ${fileName2} különbsége Nappal`;
					let newKeyNight = `${fileName1} - ${fileName2} különbsége Éjjel`;
					
					updateDifferenceColumns();
					
					switch (differenceColumnToUpdate) {
			            case 0:
			                columnMapping[newKeyDay] = 'differenceDay0';
			                columnMapping[newKeyNight] = 'differenceNight0';
			                console.log('Updated column mapping after difference:', columnMapping);		                
			                 sendFilesToBackend(fileId1, fileId2);
			                break;
			            case 1:
			                columnMapping[newKeyDay] = 'differenceDay1';
			                columnMapping[newKeyNight] = 'differenceNight1';
			                console.log('Updated column mapping after difference:', columnMapping);
			                 sendFilesToBackend(fileId1, fileId2);
			                break;
			            case 2:
			                columnMapping[newKeyDay] = 'differenceDay2';
			                columnMapping[newKeyNight] = 'differenceNight2';
			                console.log('Updated column mapping after difference:', columnMapping);
			                 sendFilesToBackend(fileId1, fileId2);
			                break;
			            default:
			                alert('Invalid difference column update.');
			                break;
			        }
					
				} else {
					alert('Maxmimum 3 különbségszámítást lehet végezni! Törölj meglévőt, ha újat szeretnél.');
				}
			}
			
			function updateDifferenceColumns() {
				// Store the previous state before modifying arrays
			    previousDifferenceColumnToUpdate = differenceColumnToUpdate;
			    previousDifferenceColumnTemp = [...differenceColumnTemp];
			    previousDifferenceColumnDefault = [...differenceColumnDefault];
			    
			    if (differenceColumnDefault.length > 0) {
			        // Sort the default column array
			        differenceColumnDefault.sort((a, b) => a - b);
			        
			        // Move the smallest element from default to temp
			        const smallestElement = differenceColumnDefault.shift();
			        differenceColumnTemp.push(smallestElement);
			        
			        // Set the value for updating columns and send to backend
			        differenceColumnToUpdate = smallestElement;

					console.log('differenceColumnToUpdate:', differenceColumnToUpdate);
					console.log('differenceCOlumnDefault', differenceColumnDefault);
					console.log('differenceColumnTemp', differenceColumnTemp);
			    }
			}
			
			function deletePredefinedColumns(fileId1, fileId2) {
			    const fileName1 = groupedData[fileId1][0].fileName;
			    const fileName2 = groupedData[fileId2][0].fileName;
			
			    const newKeyDay = fileName1 + ' - ' + fileName2 + ' különbsége Nappal';
			    const newKeyNight = fileName1 + ' - ' + fileName2 + ' különbsége Éjjel';
			
			    // Check if the columns exist in columnMapping and delete them
			    if (columnMapping[newKeyDay]) {
			        delete columnMapping[newKeyDay];
			    }
			    if (columnMapping[newKeyNight]) {
			        delete columnMapping[newKeyNight];
			    }
			}
			
			function handleBadResponse() {
			    differenceColumnToUpdate = previousDifferenceColumnToUpdate;
			    differenceColumnTemp = previousDifferenceColumnTemp;
			    differenceColumnDefault = previousDifferenceColumnDefault;
			}
			
			function sendFilesToBackend(fileId1, fileId2) {
				console.log('compare files id to backend:', fileId1, fileId2);
			    fetch(`/sortAndDifferences/differences/${fileId1}/${fileId2}`, {
			        method: 'POST',
			        headers: {
			            'Content-Type': 'application/json'
			        },
					body: JSON.stringify({ differenceColumnToUpdate })
			    })
			    .then(response => {
			        if (!response.ok) {
						// Handle deletion of predefined columns
        				deletePredefinedColumns(fileId1, fileId2);
        				handleBadResponse()
			            throw new Error('Network response was not ok');
			        }
			        			        
			        fetchDataAndRenderTabs();
			    })
			    .catch(error => {
			        console.error('There was a problem with the fetch operation:', error);
			    });
			}




		    let selectedValue = '';
		    
		    // listener for calculations div
		    function handleCalculationChange() {
			    const selectElement = document.getElementById('calculations');
			    const textFieldContainer = document.getElementById('text-field-container');
			    selectedValue = selectElement.value;
			    
			    // Show the text field if the last option is selected, hide otherwise
	            if (selectedValue === '/calculations/givenDistance') {
	                textFieldContainer.style.display = 'block';
	            } else {
	                textFieldContainer.style.display = 'none';
	            }
			}
			 function handleSubmit() {
				if (selectedValue === '/calculations/givenDistance') {
					const userInput = document.getElementById('userInput').value;
					
					if (userInput !== '') {	
						// update the columnmapping with the distance (user input)
						const keys = Object.keys(columnMapping);
						const newKeys = keys.slice(0, -2);
						const updatedColumnMapping = {};
						newKeys.forEach(key => {
						  updatedColumnMapping[key] = columnMapping[key];
						});		
						
						columnMapping = updatedColumnMapping;
						
						const newKeyDay = `Zajterhelés ${userInput} m távolságon Nappal`;
						const newKeyNight = `Zajterhelés ${userInput} m távolságon Éjjel`;

						
						columnMapping[newKeyDay] = 'noiseAtGivenDistanceDay';
						columnMapping[newKeyNight] = 'noiseAtGivenDistanceNight';

						console.log('Updated column mapping:', columnMapping);		
													
						// for givenDistance endpoint
						console.log('userInput:', userInput);
						fetch(`${selectedValue}/${activeFileId}/${userInput}`, {
							method: 'POST',
							headers: {
								'Content-Type': 'application/json'
							},
						})
						.then(response => {
							if (!response.ok) {
								throw new Error('Network response was not ok');
							}
							fetchDataAndRenderTabs();
						})
						.catch(error => {
							console.error('There was a problem with the fetch operation:', error);
						});
					} else {
						console.log('User input is empty')
					}
				} else {
					// for other endpoints
					if (selectedValue !== '' && activeFileId) {
						fetch(`${selectedValue}/${activeFileId}`, {
							method: 'POST',
							headers: {
								'Content-Type': 'application/json'
							},
						})
						.then(response => {
							if (!response.ok) {
								throw new Error('Network response was not ok');
							}
							fetchDataAndRenderTabs();
						})
						.catch(error => {
							console.error('There was a problem with the fetch operation:', error);
						});
					}
				}
			}
			
			let groupedData;
			// Function to fetch data, group by file_id, and render tabs and tables
			const fetchDataAndRenderTabs = () => {
			    fetch('/console/displayData')
			        .then(response => {
			            if (!response.ok) {
			                throw new Error('Network response was not ok');
			            }
			            return response.json();
			        })
			        .then(data => {
			            console.log('Fetched Data:', data);
			            groupedData = data
			            renderTabsAndTables(data);
			        })
			        .catch(error => {
			            console.error('There was a problem with the fetch operation:', error);
			        });
			};

			
			let activeFileId;
			let tbody;
			let tables;
			
			// Function to render tabs and tables using grouped data
			const renderTabsAndTables = (groupedData) => {
			    console.log('Grouped Data:', groupedData);
			        const tabs = Object.keys(groupedData).map(fileId => {
			        const fileName = groupedData[fileId][0].fileName;
					
			        return (
			            <span key={fileId}>
			                <button onClick={() => {
			                    renderTable(groupedData[fileId]);
			                    activeFileId = fileId; // Set the active file_id
			                }}>
			                    {fileName}
			                </button>
			            </span>
			        );
			    });
			    // Set activeFileId for the lowest file_id by default
			    const lowestFileId = Math.min(...Object.keys(groupedData));
			    activeFileId = lowestFileId;

			    ReactDOM.render(
			        <div>
			            {tabs}
			            <div id="table-container"></div>
			        </div>,
			        document.getElementById('root')
			    );
			console.log('activeFileId in renderTabsAndTables:', activeFileId);
			    // Display the table for the lowest file_id by default
			  //  const lowestFileId = Math.min(...Object.keys(groupedData));
			    renderTable(groupedData[activeFileId]);
			};
			
			// Function to render a specific table for a file
			const renderTable = (data) => {
			    console.log('Data for Table:', data);
			    
			    // Extracting the fileName from the data
    			const fileName = data.length > 0 ? data[0].fileName : '';
    			
    			// Displaying the table name (fileName) above the table
				const tableName = <h2>{fileName}</h2>;
			    
			     // Filter out fields that have null values based on columnMapping
			    const nonNullColumns = Object.entries(columnMapping)
			        .filter(([displayName, columnKey]) => {
			            // Check if the column exists in the data and if its value is not null
			            return data.some(item => item[columnKey] !== null && item[columnKey] !== undefined);
			        })
			        .map(([displayName, columnKey]) => {
			            return { displayName, columnKey };
			        });
			
			    const tableHeaders = nonNullColumns.map(({ displayName }, index) => (
			        <th key={index}>{displayName}</th>
			    ));
			
			    const tableRows = data.map((dataItem, index) => {
			        return (
			            <tr key={index}>
			                {nonNullColumns.map(({ columnKey }, idx) => {
			                    const cellValue = dataItem[columnKey];
			                    return <td key={idx}>{cellValue}</td>;
			                })}
			            </tr>
			        );
			    });
			
			    const table = (
					<div>
						{tableName}
				        <table id='dataTable'>
				            <thead>
				                <tr>
				                    {tableHeaders}
				                </tr>
				            </thead>
				            <tbody>
				                {tableRows}
				            </tbody>
				        </table>
			        </div>
			    );

			    console.log('Rendering Table:', table);
			    ReactDOM.render(table, document.getElementById('table-container'));
			    
			    tbody = document.querySelector('#dataTable tbody');
				console.log('tbody', tbody);
					
				tbody.addEventListener('click', handleCellClick);

			};
			
			// Call the function to fetch data and render tabs on page load
			fetchDataAndRenderTabs();
			
			