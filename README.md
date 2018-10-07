# cucumber-scenarios-indexer

<p>
    A little plugin which updates your Cucumber feature files 
    with indexes for scenarios 
</p>
<p>
    Works from the context menu in a project files tree - will change 
    a *.feature file or all the *.feature files in a directory and 
    all the subdirectories
</p>
<p>
    Just click "Index Cucumber Scenarios" from the context menu 
    on a folder or a file in the project files tree
</p>

<p>E.g. before: </p>
<ul>
    <li>Scenario: testing requirement #345 </li>
    <li>Scenario Outline: testing requirement #123 </li>
</ul>
<p>after: </p>
<ul>
    <li>Scenario: 01 - testing requirement #345 </li>
    <li>Scenario Outline: 02 - testing requirement #123 </li>
</ul>
<p><b>OR</b></p>
<p>before:</p>
<ul>
    <li>Scenario: 01 - testing requirement #345 </li>
    <li>Scenario Outline: 02 - testing requirement #123 </li>
    <li>Scenario: 01 - testing requirement #345 </li>
    <li>Scenario Outline: 02 - testing requirement #123 </li>
</ul>
<p>after: </p>
<ul>
    <li>Scenario: 01 - testing requirement #345 </li>
    <li>Scenario Outline: 02 - testing requirement #123 </li>
    <li>Scenario: 03 - testing requirement #345 </li>
    <li>Scenario Outline: 04 - testing requirement #123 </li>
</ul>