import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { CatService } from '../../services/cat-service';
import { Cat } from '../../models/cat-model';
import { CatCardComponent } from '../../components/cat-card/cat-card';
import { CatSearchComponent } from '../../components/cat-search/cat-search';

@Component({
  selector: 'app-cats',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatButtonModule,
    MatIconModule,
    CatCardComponent,
    CatSearchComponent
  ],
  templateUrl: './cats.html',
  styleUrl: './cats.scss'
})
export class CatsComponent implements OnInit {
  cats: Cat[] = [];
  filteredCats: Cat[] = [];
  allCats: Cat[] = [];
  loading = true;

  pageSize = 12;
  pageIndex = 0;
  totalCats = 0;

  currentFilters: any = {};

  constructor(
    private catService: CatService,
    private route: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.currentFilters = { ...params };
      this.loadAvailableCats();
    });
  }

  loadAvailableCats() {
    this.loading = true;
    this.catService.getAllCats().subscribe({
      next: (cats) => {
        if (cats) {
          this.allCats = cats.filter(cat => cat.adoptionStatus === 'AVAILABLE');
          this.applyFilters();
        }
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading cats:', error);
        this.loading = false;
        this.allCats = this.getMockCats();
        this.applyFilters();
      }
    });
  }

  onFiltersChanged(filters: any) {
    console.log('Received filters from CatSearchComponent:', filters);
    this.currentFilters = filters;
    this.pageIndex = 0;
    
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: filters,
      queryParamsHandling: 'replace'
    });
    
    this.applyFilters();
  }

  applyFilters() {
    let filtered = [...this.allCats];

    if (this.currentFilters && Object.keys(this.currentFilters).length > 0) {
      filtered = filtered.filter(cat => {
        // Filter by breed name
        if (this.currentFilters.breed && this.currentFilters.breed !== '') {
          if (cat.breedName !== this.currentFilters.breed) {
            return false;
          }
        }
        // Filter by gender
        if (this.currentFilters.gender && this.currentFilters.gender !== '') {
          if (cat.gender !== this.currentFilters.gender) {
            return false;
          }
        }
        // Filter by age range
        if (this.currentFilters.ageRange && this.currentFilters.ageRange !== '') {
          const age = cat.age;
          let ageMatch = true;
          
          switch (this.currentFilters.ageRange) {
            case '0-2':
              ageMatch = age >= 0 && age <= 2;
              break;
            case '3-7':
              ageMatch = age >= 3 && age <= 7;
              break;
            case '8+':
              ageMatch = age >= 8;
              break;
          }
          
          if (!ageMatch) {
            return false;
          }
        }

        return true;
      });
    }

    this.filteredCats = filtered;
    this.totalCats = filtered.length;
    this.cats = filtered;

    console.log(`Applied filters. Showing ${this.totalCats} cats out of ${this.allCats.length} total`);
  }

  onPageChange(event: PageEvent) {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
  }

  get pagedCats(): Cat[] {
    const startIndex = this.pageIndex * this.pageSize;
    return this.filteredCats.slice(startIndex, startIndex + this.pageSize);
  }

  clearFilters() {
    console.log('Clearing all filters');
    this.currentFilters = {};
    this.pageIndex = 0;
    
    // Clear URL params
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {},
    });
    this.applyFilters();
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
        imageUrl: 'https://cdn2.thecatapi.com/images/O3btzLlsO.png',
        createdAt: '2024-01-15T10:00:00Z',
        updatedAt: '2024-01-15T10:00:00Z'
      },
      {
        id: 2,
        name: 'Max',
        age: 5,
        gender: 'MALE',
        breedId: 'siam',
        breedName: 'Siamese',
        description: 'Playful and energetic cat, loves to chase toys',
        adoptionStatus: 'AVAILABLE',
        latitude: 53.124,
        longitude: 14.223,
        address: 'Dresden',
        imageUrl: 'https://cdn2.thecatapi.com/images/O3btzLlsO.png',
        createdAt: '2024-01-15T10:00:00Z',
        updatedAt: '2024-01-15T10:00:00Z'
      },
      {
        id: 3,
        name: 'Bella',
        age: 8,
        gender: 'FEMALE',
        breedId: 'mcoo',
        breedName: 'Maine Coon',
        description: 'Senior cat with lots of love to give',
        adoptionStatus: 'AVAILABLE',
        latitude: 53.124,
        longitude: 14.223,
        address: 'Leipzig',
        imageUrl: 'https://cdn2.thecatapi.com/images/O3btzLlsO.png',
        createdAt: '2024-01-15T10:00:00Z',
        updatedAt: '2024-01-15T10:00:00Z'
      },
      {
        id: 4,
        name: 'Charlie',
        age: 1,
        gender: 'MALE',
        breedId: 'pers',
        breedName: 'Persian Mix',
        description: 'Young and playful kitten looking for an active family',
        adoptionStatus: 'AVAILABLE',
        latitude: 53.124,
        longitude: 14.223,
        address: 'Munich',
        imageUrl: 'https://cdn2.thecatapi.com/images/O3btzLlsO.png',
        createdAt: '2024-01-15T10:00:00Z',
        updatedAt: '2024-01-15T10:00:00Z'
      }
    ];
  }
}
