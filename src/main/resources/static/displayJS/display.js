
 let columnMapping = {};
 
	 // Function to get column mappings for a specific file
	function getColumnMapping(fileId) {
	  const data = JSON.parse(sessionStorage.getItem(`columnMappings_${fileId}`));
	  console.log('GET fileId', `columnMappings_${fileId}`);
	  console.log('columnMap in GET:', data);
	  return data ? data : getDefaultMapping();
	}
	
	// Function to save column mappings for a specific file
	function saveColumnMapping(fileId, mappings) {
	  sessionStorage.setItem(`columnMappings_${fileId}`, JSON.stringify(mappings));
	}
 
	function getDefaultMapping() {
	    return {
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
			};
		}
    
		    
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
	        
	const parameterMapping = {
	    'lthDay': 'Határérték Nappal',
	    'lthNight': 'Határérték Éjjel',
	    'roadSurfaceRoughness': 'K (útburkolati érdesség)',
	    'reflection': 'Kr (reflexió)',
	    'soundAbsorptionFactor': 'C (hangelnyelési tényező)',
	    'angleOfView': 'Látószög',
	    'trafficType': 'Forgalom típusa',
	    'slopeElevation': 'Lejtés/emelkedés (%-ban)',
	};
	
	const selectableValues = {
	    'lthDay': [50.0, 55.0, 60.0, 65.0],
	    'lthNight': [40.0, 45.0, 50.0, 55.0],
	    'roadSurfaceRoughness': [0.0, 0.29, 0.49, 0.67, 0.78],
	    'reflection': [0.5, 1.0, 1.5, 2.0, 2.5, 3.5],
	    'soundAbsorptionFactor': [12.5, 15.0],
	    'trafficType' : ['egyenletes', 'lassuló', 'gyorsuló']
	};
	

	const userInputValues = {
		'angleOfView' : true,
		'slopeElevation' : true
	};

    let differenceColumnDefault = [0, 1, 2];
    let differenceColumnTemp = [];
    let differenceColumnToUpdate = 0;
    
    let previousDifferenceColumnToUpdate;
	let previousDifferenceColumnTemp;
	let previousDifferenceColumnDefault;
			
	// handle ProtectiveDistance Save
	function handleProtectiveDistanceSave() {
		if (groupedData[activeFileId] === null) {
			return;
		}
		if (groupedData[activeFileId][0].protectiveDistanceDay === null) {
			alert('Védőtávolság Nappal mező nincs kiszámolva!');
			return;
		}
		if (groupedData[activeFileId][0].protectiveDistanceNight === null) {
			alert('Védőtávolság Éjjel mező nincs számolva!');
			return;
		}
		
		const fileName = groupedData[activeFileId][0].fileName;
		
		fetch(`/console/saveProtectiveDistance/${activeFileId}/${fileName}`,{
			method: 'POST',
			headers: {
				'Content-type': 'application/json'
			},
		})
		.then(async (response) => {
			if (response.ok) {
	            const blob = await response.blob();
	            const url = window.URL.createObjectURL(blob);
	
	            // Create an anchor element
	            const a = document.createElement('a');
	            a.href = url;
	            a.download = `${fileName}_vedotav.zip`; 
	            document.body.appendChild(a);
	            a.click();
	            a.remove();
	            window.URL.revokeObjectURL(url);
        	}
		})
		.catch((error) => {
			console.error('Error in saving the file:', error);
		});
	}
			
	// handle File Save
	function handleFileSave() {
		console.log('groupedData[activeFileId]', groupedData[activeFileId]);
		console.log('groupedData[activeFileId][0]', groupedData[activeFileId][0]);
		if (groupedData[activeFileId] === null) {
			return;
		}
		if (groupedData[activeFileId][0].lwDay === null) {
			alert('LW Nappal mező nincs kiszámolva!');
			return;
		}
		if (groupedData[activeFileId][0].lwNight === null) {
			alert('LW Éjjel mező nincs számolva!');
			return;
		}
		
		const activeFileData = groupedData[activeFileId][0];

	    const columnNames = Object.keys(columnMapping).filter(key => {
		    const dataKey = columnMapping[key];
		    return activeFileData[dataKey] !== null && activeFileData[dataKey] !== undefined;
		});
		
		const fileName = groupedData[activeFileId][0].fileName;
	    
	    console.log('columnNames', columnNames);
		
		fetch(`/console/saveFile/${activeFileId}/${fileName}`,{
			method: 'POST',
			headers: {
				'Content-type': 'application/json'
			},
			body: JSON.stringify(columnNames)
		})
		.then(async (response) => {
			if (response.ok) {
	            const blob = await response.blob();
	            const url = window.URL.createObjectURL(blob);
	
	            // Create an anchor element
	            const a = document.createElement('a');
	            a.href = url;
	            a.download = `${fileName}.zip`;
	            document.body.appendChild(a);
	            a.click();
	            a.remove();
	            window.URL.revokeObjectURL(url);
        	}
		})
		.catch((error) => {
			console.error('Error in saving the file:', error);
		});
		
	}
				
			// after modifications let the calculations run again
			let isModificationHappened = false;

			// handle data modifications
			let isCellEditing = false;		
			let singleClickTimer;
					
	const handleCellDoubleClick = async (cell) => {
	    const activeData = groupedData[activeFileId];
	    const row = cell.parentNode.rowIndex;
	    
		console.log('double clicked on cell');
		console.log('row', row);
		console.log('isCellEditing', isCellEditing);
		
	    // Fetch data for the form
	    const response = await fetch(`/modification/getMutableParameters/${activeFileId}/${row}`);
	    const data = await response.json();
	    
		console.log('data from getMutableParamaters', data);
		
	    // Create a form with fetched data
	    const form = document.createElement('form');
	    form.classList.add('form');
	
	    // Iterate over the parameterMapping keys and create form fields
	    Object.keys(parameterMapping).forEach((key) => {
	        const label = document.createElement('label');
	        label.textContent = parameterMapping[key];
	        
			console.log('label - parametermapping', label);
			
	        if (userInputValues[key]) {
	            // Create input fields for userInputValues
	            const input = document.createElement('input');
	            input.name = key;
	            console.log('input - userinputvalue', input);
	            input.type = 'text';
	            // Set the input value based on the fetched data
	            input.value = data[key] || '';
	
	            // Append the input field to the form
	            label.appendChild(input);
	        } else {
	            // Create select dropdowns for selectableValues
	            const select = document.createElement('select');
	            select.name = key;
	            // Populate options for select dropdown based on selectableValues
	            selectableValues[key].forEach((value) => {
	                const option = document.createElement('option');
	                option.value = value;
	                option.textContent = value;
	                // Set the selected value based on the fetched data
	                if (data[key] === value) {
	                    option.selected = true;
	                }
	                select.appendChild(option);
	            });
	
	            // Append the select dropdown to the form
	            label.appendChild(select);
	        }
	
	        // Append the label with input/select to the form
	        form.appendChild(label);
	
	    });
	
	    // Create buttons for submit and cancel
	    const submitButton = document.createElement('button');
	    submitButton.textContent = 'Submit';
	    submitButton.addEventListener('click', () => {
			
	        console.log('before submit');
	        
	        handleSubmit();
	        isCellEditing = false;
	        form.remove();
	    });
	    
	    const handleSubmit = () => {
	    const formData = {};
	
	    // Iterate through all form elements
	    const formElements = form.elements;
	    for (let i = 0; i < formElements.length; i++) {
	        const element = formElements[i];
	        if (element.tagName === 'INPUT' || element.tagName === 'SELECT') {
	            formData[element.name] = element.value;
	        }
	    }
		console.log('during submit, activeFileId, rowNumber, formData:', activeFileId, row, formData);

	    fetch(`/modification/setMutableParameters/${activeFileId}/${row}`, {
	        method: 'PUT',
	        headers: {
	            'Content-Type': 'application/json'
	        },
	        body: JSON.stringify(formData)
		    }).then((response) => {
		        console.log('Mutable Parameters modified for the row.', response);
		    }).catch((error) => {
		        console.error('There was a problem with the fetch operation:', error);
		    });
		};

	    const cancelButton = document.createElement('button');
	    cancelButton.textContent = 'Cancel';
	    cancelButton.addEventListener('click', () => {
	        // Handle cancel action - simply remove the form from the DOM
	        isCellEditing = false;
	        form.remove();
	    });

	    // Append buttons to the form
	    form.appendChild(submitButton);
	    form.appendChild(cancelButton);  
	   
       // Display the form under the clicked row in the table
	    const tableContainer = document.getElementById('table-container');
	    const dataTable = tableContainer.querySelector('#dataTable');
	    const clickedRow = cell.parentNode;
	
	    // Create a new row and cell to hold the form
	    const formRow = dataTable.insertRow(clickedRow.rowIndex + 1); // Insert new row after the clicked row
	    const formCell = formRow.insertCell(0); // Create a cell in the new row
	
	    // Append the form to the cell
	    formCell.appendChild(form);
	};
					
			const handleCellClick = (cell) => {
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
			                saveColumnMapping(activeFileId, columnMapping);
			                console.log('Updated column mapping after difference:', columnMapping);		                
			                 sendFilesToBackend(fileId1, fileId2);
			                break;
			            case 1:
			                columnMapping[newKeyDay] = 'differenceDay1';
			                columnMapping[newKeyNight] = 'differenceNight1';
			                saveColumnMapping(activeFileId, columnMapping);
			                console.log('Updated column mapping after difference:', columnMapping);
			                 sendFilesToBackend(fileId1, fileId2);
			                break;
			            case 2:
			                columnMapping[newKeyDay] = 'differenceDay2';
			                columnMapping[newKeyNight] = 'differenceNight2';
			                saveColumnMapping(activeFileId, columnMapping);
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
					console.log('userInput for noiseAtGivenDistance:', userInput);
					if (userInput !== '') {					
						// Check and delete the columns if they exist in columnMapping
						function valueExists(mapping, value) {
						    return Object.values(mapping).includes(value);
						}
						
						if (valueExists(columnMapping, 'noiseAtGivenDistanceDay')) {
							console.log('deletion triggerend in columnmapping');
						    deleteMappingByValue(columnMapping, 'noiseAtGivenDistanceDay');
						    console.log('columnMapping after deletion:', columnMapping);
						}
						
						if (valueExists(columnMapping, 'noiseAtGivenDistanceNight')) {
						    deleteMappingByValue(columnMapping, 'noiseAtGivenDistanceNight');
						}
						console.log('columnMapping after check of previous existence', columnMapping);
						// Add the updated columns
						
						
						
						function deleteMappingByValue(mapping, valueToDelete) {
						    const keysToDelete = Object.keys(mapping).filter(key => mapping[key] === valueToDelete);
						
						    keysToDelete.forEach(key => {
						        delete mapping[key];
						    });
						}						
			            // other mappings
			            const existingKeys = Object.keys(columnMapping);
						const indexOfImpactAreaNight = existingKeys.indexOf('Hatásterület Éjjel');
						
						const keysBeforeImpactArea = existingKeys.slice(0, indexOfImpactAreaNight + 1);
						const keysAfterImpactArea = existingKeys.slice(indexOfImpactAreaNight + 1);
						console.log('keysBeforeImpactArea', keysBeforeImpactArea);
						console.log('keysAfterImpactArea', keysAfterImpactArea);
						
						
						// Create a new mapping with the original keys before 'Hatásterület Éjjel'
						const updatedColumnMapping = {};
						
						keysBeforeImpactArea.forEach((key) => {
						    updatedColumnMapping[key] = columnMapping[key];
						    console.log('updatedColumnMapping loop:', updatedColumnMapping);
						});
						console.log('updatedColumnMapping before check:', updatedColumnMapping);
						console.log('columnMapping before check of previous existence', columnMapping);
	
						// Create the new mapping for the two columns to add
						const newKeyDay = `Zajterhelés ${userInput} m távolságon Nappal`;
						const newKeyNight = `Zajterhelés ${userInput} m távolságon Éjjel`;
						updatedColumnMapping[newKeyDay] = 'noiseAtGivenDistanceDay';
						updatedColumnMapping[newKeyNight] = 'noiseAtGivenDistanceNight';
						
						console.log('updatedColumnMapping before remaining map:', updatedColumnMapping);
						
						// Add the remaining columns after the new ones
						keysAfterImpactArea.forEach((key) => {
						    updatedColumnMapping[key] = columnMapping[key];
						});
						
						console.log('updatedColumnMapping before assigning to columnMapping:', updatedColumnMapping);
			            columnMapping = updatedColumnMapping;
			            saveColumnMapping(activeFileId, columnMapping);
													
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
			let file_id;
			// Function to render tabs and tables using grouped data
			const renderTabsAndTables = (groupedData) => {
			    console.log('Grouped Data:', groupedData);
			    
			        const tabs = Object.keys(groupedData).map(fileId => {
					file_id = fileId;
			        const fileName = groupedData[fileId][0].fileName;
					
			        return (
			            <span key={fileId}>
			                <button onClick={() => {
								activeFileId = fileId; // Set the active file_id
								console.log('activeFileId on tabClick', activeFileId);
			                    renderTable(groupedData[fileId]);		                    
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
				columnMapping = getColumnMapping(activeFileId);

			    console.log('Data for Table:', data);
			    console.log('columnMapping before rendering', columnMapping);
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
					
				// Event listener for table cell clicks
				tbody.addEventListener('click', (e) => {
				    if (!isCellEditing) {
				        // Check for double click within a timeframe (300ms in this example)
				        clearTimeout(singleClickTimer);
				        singleClickTimer = setTimeout(() => {
				            handleCellClick(e.target.closest('td'));
				        }, 300); // Adjust the delay here (in milliseconds)
				    }
				});
				
				// Event listener for table cell double-clicks
				tbody.addEventListener('dblclick', (e) => {
				    clearTimeout(singleClickTimer);
				    if (!isCellEditing) {
				        isCellEditing = true; // Set the flag to true to prevent subsequent single-clicks
				        handleCellDoubleClick(e.target.closest('td'));
				        setTimeout(() => {
				        }, 500);
				    }
				});

			};
			
			// Call the function to fetch data and render tabs on page load
			fetchDataAndRenderTabs();
			
			