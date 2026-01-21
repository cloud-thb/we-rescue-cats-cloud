import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { CatService } from '../../services/cat-service';
import { Cat, CatBreed } from '../../models/cat-model';
import { CatCardComponent } from '../../components/cat-card/cat-card';
import { MapComponent } from '../../components/map/map';
import { StatsComponent } from '../../components/stats/stats';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatProgressSpinnerModule,
    CatCardComponent,
    StatsComponent,
    MapComponent
  ],
  templateUrl: './home.html',
  styleUrl: './home.scss'
})
export class HomeComponent implements OnInit {
  featuredCats: Cat[] = [];
  stats: any = {
    catsRescued: 2847,
    happyFamilies: 1923,
    rescuePartners: 156,
    support: '24/7'
  };
  loading = true;
  popularBreeds: CatBreed[] = [];
  popularBreedsIds = ['pers', 'siam', 'mcoo', 'bsho', 'ragd', 'rblu', 'sphy', 'sfol'];
  successStories = [
    {
      quote: "Luna has brought so much joy to our family. She's the perfect companion for our kids and has settled in beautifully.",
      author: "Max Mustermann, Dresden",
      adoptedInfo: "Adopted Luna (Persian Mix)"
    },
    {
      quote: "Coco is incredible! He gets along perfectly with our dog and has such a playful personality. Best decision ever!",
      author: "Maximus Termann, Leipzig",
      adoptedInfo: "Adopted Coco (Maine Coon)"
    },
    {
      quote: "Adopting Mia was the best choice. She filled our home with love and laughter.",
      author: "Mustermann Max, Magdeburg",
      adoptedInfo: "Adopted Mia"
    }
  ];

  constructor(private catService: CatService) {}

  ngOnInit() {
    this.loadFeaturedCats();
    this.loadPopularBreeds();
  }

  onFiltersChanged(filters: any) {
    console.log('Filters changed:', filters);
  }

  loadFeaturedCats() {
    this.catService.getAllCats().subscribe({
      next: (cats) => {
        if (cats) {
          this.featuredCats = cats.filter(cat => cat.adoptionStatus === 'AVAILABLE').slice(0, 6);
        }
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading featured cats:', error);
        this.loading = false;
        this.featuredCats = this.getMockCats();
      }
    });
  }

  loadPopularBreeds() {
    this.catService.getCatBreeds().subscribe({
      next: (allBreeds: CatBreed[]) => {
        const popularIds = new Set(this.popularBreedsIds);
        this.popularBreeds = allBreeds.filter(breed => popularIds.has(breed.id));
      },
      error: (error) => {
        console.error('Error loading popular breeds:', error);
      }
    });
  }

  private getMockCats(): Cat[] {
    return [
      {
        id: 1,
        name: 'Luna',
        age: 2,
        gender: 'FEMALE',
        breedId: 'pers',
        breedName: 'Persian Mix',
        description: 'Sweet and gentle cat looking for a loving home',
        adoptionStatus: 'AVAILABLE',
        latitude: 53.124,
        longitude: 14.223,
        address: 'Dresden',
        imageUrl: 'https://via.placeholder.com/300x250?text=Luna',
        createdAt: '2024-01-15T10:00:00Z',
        updatedAt: '2024-01-15T10:00:00Z'
      },
      {
        id: 2,
        name: 'Coco',
        age: 4,
        gender: 'MALE',
        breedId: 'main',
        breedName: 'Maine Coon',
        description: 'Playful and friendly, great with dogs',
        adoptionStatus: 'AVAILABLE',
        latitude: 51.233,
        longitude: 13.987,
        address: 'Dresden',
        imageUrl: 'https://via.placeholder.com/300x250?text=Max',
        createdAt: '2024-01-10T10:00:00Z',
        updatedAt: '2024-01-10T10:00:00Z'
      }
    ];
  }
}
