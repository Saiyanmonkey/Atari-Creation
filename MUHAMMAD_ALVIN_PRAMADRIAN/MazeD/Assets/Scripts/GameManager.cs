using System.Collections;
using System.Collections.Generic;
using System.IO;
using UnityEngine;

public class GameManager : MonoBehaviour
{
    public Maze mazePrefabs;

    private Maze mazeInstance;

    // Start is called before the first frame update
    void Start()
    {
        BeginGame();
    }

    // Update is called once per frame
    void Update() {
        if (Input.GetKeyDown(KeyCode.Space))
        {
            RestartGame();
        }
    }

    private void BeginGame() {
        mazeInstance = Instantiate(mazePrefabs) as Maze;
        StartCoroutine(mazeInstance.Generate());
    }

    private void RestartGame() {
        StopAllCoroutines();
        Destroy(mazeInstance.gameObject);
        BeginGame();
    }
}
